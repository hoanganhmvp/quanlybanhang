package com.example.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.CartItem;
import com.example.entity.Product;
import com.example.entity.ReturnedInventory;
import com.example.entity.SalesDetail;
import com.example.entity.SalesOrder;
import com.example.entity.SalesReturn;
import com.example.entity.User;
import com.example.entity.Voucher;
import com.example.repository.CartItemRepository;
import com.example.repository.ProductRepository;
import com.example.repository.ReturnedInventoryRepository;
import com.example.repository.SalesOrderRepository;
import com.example.repository.SalesReturnRepository;
import com.example.repository.UserRepository;
import com.example.repository.VoucherRepository;

@Service
public class OrderService {

    @Autowired private SalesOrderRepository orderRepo;
    @Autowired private CartItemRepository cartRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private SalesReturnRepository returnRepo;
    @Autowired private VoucherRepository voucherRepo;
    @Autowired private ReturnedInventoryRepository returnedInventoryRepo;
    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Transactional
    public SalesOrder checkout() {
        // Get current user from authentication context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Sử dụng currentUser.getId() để quét giỏ hàng
        List<CartItem> cartItems = cartRepo.findByUserId(currentUser.getId());
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        // 2. Khởi tạo Đơn hàng (Chung 1 mã hóa đơn)
        SalesOrder order = new SalesOrder();
        order.setUser(currentUser);
        order.setOrderCode("INV-" + System.currentTimeMillis());
        order.setStatus("PENDING"); // Logic: Mặc định là chờ Nhân viên duyệt
        order.setOrderDate(LocalDateTime.now());

        BigDecimal grandTotal = BigDecimal.ZERO;
        List<SalesDetail> details = new ArrayList<>();

        // 3. Duyệt giỏ hàng để tạo Chi tiết hóa đơn
        for (CartItem item : cartItems) {
            Product p = item.getProduct();
            
            // Check tồn kho
            if (p.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + p.getName() + " không đủ hàng!");
            }

            // Trừ tồn kho tạm thời
            int newStock = p.getStock() - item.getQuantity();
            p.setStock(newStock);
            productRepo.save(p);
            // Tạo detail
            SalesDetail detail = new SalesDetail();
            detail.setOrder(order);
            detail.setProduct(p);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(p.getPrice()); 

            BigDecimal subTotal = p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            grandTotal = grandTotal.add(subTotal);
            details.add(detail);
        }

        order.setDetails(details);
        order.setTotalAmount(grandTotal); 

        // 4. Xóa giỏ hàng sau khi đã lên đơn
        cartRepo.deleteByUserId(currentUser.getId());

        return orderRepo.save(order);
    }

    @Transactional
    public void requestReturn(Integer orderId, Integer productId, Integer qty, String reason) {
        String currentEmail = getCurrentUserEmail();
        
        SalesOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        if (!order.getUser().getEmail().equals(currentEmail)) {
            throw new RuntimeException("Lỗi bảo mật: Bạn không có quyền yêu cầu trả hàng cho hóa đơn của người khác!");
        }
                if (!"PAID".equals(order.getStatus()) && !"DELIVERED".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ được trả hàng khi đơn đã thanh toán hoặc đã giao");
        }

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (qty == null || qty <= 0) {
            throw new RuntimeException("Số lượng trả hàng phải lớn hơn 0");
        }

        SalesReturn salesReturn = new SalesReturn();
        salesReturn.setOrder(order);
        salesReturn.setProduct(product);
        salesReturn.setQuantity(qty);
        salesReturn.setReason(reason);
        salesReturn.setStatus("REQUESTED");
        salesReturn.setCreatedAt(LocalDateTime.now());

        returnRepo.save(salesReturn);

        order.setStatus("PENDING_RETURN");
        orderRepo.save(order);
    }

   @Transactional
public void confirmReturn(Integer returnId, Integer employeeId) {
    // 1. Tìm yêu cầu trả hàng
    SalesReturn res = returnRepo.findById(returnId)
            .orElseThrow(() -> new RuntimeException("Yêu cầu trả hàng không tồn tại"));

    if (res.getStatus().equals("COMPLETED")) {
        throw new RuntimeException("Yêu cầu này đã được xử lý rồi");
    }

    // --- LOGIC MỚI: KHÔNG CỘNG LẠI VÀO KHO BÁN (TABLE products) ---
    // Chúng ta giữ nguyên p.setStock, không gọi đến nó nữa.

    // --- LOGIC MỚI: ĐẨY VÀO KHO HÀNG TRẢ (TABLE returned_inventory) ---
    ReturnedInventory ri = new ReturnedInventory();
    ri.setProduct(res.getProduct());
    ri.setQuantity(res.getQuantity());
    ri.setReason(res.getReason());
    ri.setReturnDate(java.time.LocalDateTime.now());
    
    returnedInventoryRepo.save(ri);

    // 2. Cập nhật trạng thái yêu cầu trả hàng
    res.setStatus("COMPLETED");
    res.setProcessedBy(userRepo.findById(employeeId).get());
    returnRepo.save(res);

    // 3. Cập nhật trạng thái đơn hàng
    SalesOrder order = res.getOrder();
    order.setStatus("RETURNED");
    orderRepo.save(order);
}

    @Transactional
    public void confirmPayment(Integer orderId) {
        // Logic: Nhân viên/Admin xác nhận hóa đơn
        SalesOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không thấy đơn hàng"));
        
        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Đơn hàng không ở trạng thái chờ xác nhận");
        }

        order.setStatus("PAID"); 
        orderRepo.save(order);
    }
@Transactional
public SalesOrder checkout(String voucherCode) {
    // 1. Lấy thông tin User từ Token (Như phần trước)
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User currentUser = userRepo.findByEmail(email).get();

    List<CartItem> cartItems = cartRepo.findByUserId(currentUser.getId());
    if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

    // 2. Tính tổng tiền tạm tính (Sub-total) và Trừ kho
    BigDecimal subTotal = BigDecimal.ZERO;
    List<SalesDetail> details = new ArrayList<>();
    
    SalesOrder order = new SalesOrder();
    order.setUser(currentUser);
    order.setOrderCode("INV-" + System.currentTimeMillis());
    order.setStatus("PENDING");
    order.setOrderDate(LocalDateTime.now());

    for (CartItem item : cartItems) {
        Product p = item.getProduct();
        if (p.getStock() < item.getQuantity()) throw new RuntimeException("Sản phẩm " + p.getName() + " hết hàng!");
        
        p.setStock(p.getStock() - item.getQuantity()); // Trừ kho
        productRepo.save(p);

        SalesDetail detail = new SalesDetail();
        detail.setOrder(order);
        detail.setProduct(p);
        detail.setQuantity(item.getQuantity());
        detail.setPrice(p.getPrice());

        subTotal = subTotal.add(p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        details.add(detail);
    }

    // 3. XỬ LÝ VOUCHER (Nếu có)
    BigDecimal discount = BigDecimal.ZERO;
    if (voucherCode != null && !voucherCode.isBlank()) {
        Voucher v = voucherRepo.findByCode(voucherCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        // KIỂM TRA ĐIỀU KIỆN VOUCHER
        if (!v.getActive()) throw new RuntimeException("Mã giảm giá đã bị vô hiệu hóa");
        if (v.getExpiryDate() != null && v.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }
        if (v.getUsedCount() >= v.getMaxUsage()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }
        if (subTotal.compareTo(v.getMinOrderAmount()) < 0) {
            throw new RuntimeException("Đơn hàng không đạt giá trị tối thiểu " + v.getMinOrderAmount() + " để dùng mã này");
        }

        // TÍNH TOÁN SỐ TIỀN GIẢM
        if ("PERCENTAGE".equalsIgnoreCase(v.getType())) {
            // Giảm theo % (VD: 10% của 1.000.000 = 100.000)
            discount = subTotal.multiply(v.getDiscountValue()).divide(new BigDecimal(100));
        } else {
            // Giảm số tiền cố định (VD: Giảm trực tiếp 50.000)
            discount = v.getDiscountValue();
        }

        // Cập nhật lượt dùng của Voucher
        v.setUsedCount(v.getUsedCount() + 1);
        voucherRepo.save(v);
        
        order.setVoucher(v);
        order.setDiscountAmount(discount);
    }

    // 4. Tính tổng tiền cuối cùng (Grand-total)
    BigDecimal grandTotal = subTotal.subtract(discount);
    if (grandTotal.compareTo(BigDecimal.ZERO) < 0) grandTotal = BigDecimal.ZERO; // Không cho phép âm tiền

    order.setTotalAmount(grandTotal);
    order.setDetails(details);

    cartRepo.deleteByUserId(currentUser.getId());
    return orderRepo.save(order);
}
public List<SalesOrder> getMyOrderHistory() {
    // 1. Lấy Email từ Token
    String email = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();
    
    // 2. Tìm User
    User user = userRepo.findByEmail(email).get();

    // 3. Truy vấn danh sách đơn hàng của User này, sắp xếp mới nhất lên đầu
    return orderRepo.findByUserOrderByIdDesc(user);
}
}