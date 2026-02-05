package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.entity.*;
import com.example.repository.*;

@Service
public class OrderService {

    @Autowired private SalesOrderRepository orderRepo;
    @Autowired private SalesDetailRepository detailRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private UserRepository userRepo; 
    @Autowired private VoucherRepository voucherRepo; 
    @Transactional
    public SalesOrder createOrder(SalesOrder order, String voucherCode) {
        // 1. Tìm user thật từ DB
        User user = userRepo.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        order.setUser(user);

        // 2. Gán thông tin cơ bản cho đơn hàng
        order.setOrderCode("ORD-" + System.currentTimeMillis());
        order.setStatus("PENDING");

        BigDecimal total = BigDecimal.ZERO;

        // 3. Duyệt danh sách chi tiết để tính tiền và gán liên kết
        if (order.getDetails() != null) {
            for (SalesDetail d : order.getDetails()) {
                // Lấy sản phẩm từ DB để có giá chính xác nhất
                Product p = productRepo.findById(d.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
                        if (p.getStock() < d.getQuantity()) {
            throw new RuntimeException("Sản phẩm " + p.getName() + " không đủ hàng!");
        }
        p.setStock(p.getStock() - d.getQuantity()); // Trừ số lượng tồn kho
        productRepo.save(p); // Lưu lại sản phẩm đã trừ kho vào DB
                        d.setOrder(order); // << QUAN TRỌNG: Dòng này giúp order_id không bị NULL
                d.setPrice(p.getPrice()); // Lấy giá từ bảng product gán vào bảng detail

                // Tính tổng tiền: price * quantity
                BigDecimal subTotal = p.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()));
                total = total.add(subTotal);
            }
        }
          // --- SỬA PHẦN VOUCHER Ở ĐÂY ---
        if (order.getVoucher() != null && order.getVoucher().getCode() != null) {
            String code = order.getVoucher().getCode();
            
            // Tìm voucher THẬT từ DB để tránh lỗi Transient
            Voucher voucher = voucherRepo.findByCode(code)
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

            // Kiểm tra điều kiện Voucher
            if (!voucher.getActive() || (voucher.getExpiryDate() != null && voucher.getExpiryDate().isBefore(LocalDateTime.now()))) {
                throw new RuntimeException("Mã giảm giá đã hết hạn hoặc bị vô hiệu hóa");
            }
            if (voucher.getUsedCount() >= voucher.getMaxUsage()) {
                throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
            }
            if (total.compareTo(voucher.getMinOrderAmount()) < 0) {
                throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu: " + voucher.getMinOrderAmount());
            }

            // Tính số tiền giảm
            BigDecimal discount = BigDecimal.ZERO;
            if ("PERCENTAGE".equals(voucher.getType())) {
                discount = total.multiply(voucher.getDiscountValue()).divide(new BigDecimal(100));
            } else {
                discount = voucher.getDiscountValue();
            }

            total = total.subtract(discount); 
            if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

            order.setVoucher(voucher); // Gán lại voucher đã được DB quản lý vào order
            voucher.setUsedCount(voucher.getUsedCount() + 1);
            voucherRepo.save(voucher);
        } else {
            order.setVoucher(null); // Nếu không gửi mã thì xóa voucher rác để không lỗi
        }
        // ------------------------------

        order.setTotalAmount(total);

        // 4. Lưu đơn hàng
        return orderRepo.save(order);
    }

    @Transactional
    public void cancelOrder(Integer orderId){
        SalesOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if(order.getStatus().equals("CANCELLED"))
            throw new RuntimeException("Đơn hàng đã được hủy trước đó");

        // Hoàn lại kho khi hủy đơn
        for(SalesDetail d : order.getDetails()){
            Product p = d.getProduct();
            p.setStock(p.getStock() + d.getQuantity());
            productRepo.save(p);
        }

        order.setStatus("CANCELLED");
        orderRepo.save(order);
    }
@Transactional
public void confirmPaid(Integer orderId) {
    SalesOrder order = orderRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    
    if ("PENDING".equals(order.getStatus())) {
        order.setStatus("PAID");
        orderRepo.save(order);
    }
}
@Autowired private SalesReturnRepository returnRepo;
@Transactional
public void processReturn(Integer orderId, Integer productId, Integer qty, String reason) {
    // 1. Tìm đơn hàng
    SalesOrder order = orderRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

    // 2. Tìm sản phẩm
    Product product = productRepo.findById(productId)
            .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

    // 3. Lấy người thực hiện (Admin/Employee đang đăng nhập)
    String currentUsername = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();
    User admin = userRepo.findByEmail(currentUsername).get();

    // 4. LOGIC QUAN TRỌNG: Hoàn kho (Cộng lại số lượng vào bảng Product)
    product.setStock(product.getStock() + qty);
    productRepo.save(product);

    // 5. Lưu lịch sử trả hàng
    SalesReturn salesReturn = new SalesReturn();
    salesReturn.setOrder(order);
    salesReturn.setProduct(product);
    salesReturn.setQuantity(qty);
    salesReturn.setReason(reason);
    salesReturn.setReturnDate(LocalDateTime.now());
    salesReturn.setProcessedBy(admin);
    returnRepo.save(salesReturn);

    // 6. Cập nhật trạng thái đơn hàng thành RETURNED (hoặc PARTIALLY_RETURNED)
    order.setStatus("RETURNED");
    orderRepo.save(order);
}

}