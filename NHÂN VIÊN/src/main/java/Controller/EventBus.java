package Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * EventBus - Simple event system để giao tiếp giữa các controller
 * 
 * Ví dụ sử dụng:
 *   - Publish: EventBus.publish(EventBus.EVENT_SERVICE_ORDER_CREATED);
 *   - Subscribe: EventBus.subscribe(eventType -> { ... });
 * 
 * Lợi ích:
 *   ✓ Loosely coupled - các controller không phụ thuộc lẫn nhau
 *   ✓ Dễ mở rộng - thêm event mới dễ dàng
 *   ✓ Clean code - không cần tìm kiếm controller trong scene graph
 */
public class EventBus {
    private static final List<Consumer<String>> listeners = new ArrayList<>();
    
    // ========== EVENT TYPES ==========
    /** Khi tạo hóa đơn đặt sân thành công */
    public static final String EVENT_INVOICE_CREATED = "invoice_created";
    
    /** Khi hóa đơn thanh toán thành công */
    public static final String EVENT_INVOICE_PAID = "invoice_paid";
    
    /** Khi tạo đơn dịch vụ thành công */
    public static final String EVENT_SERVICE_ORDER_CREATED = "service_order_created";
    
    /** Khi cập nhật tồn kho dịch vụ */
    public static final String EVENT_SERVICE_STOCK_UPDATED = "service_stock_updated";
    
    /** Khi xóa hóa đơn */
    public static final String EVENT_INVOICE_DELETED = "invoice_deleted";
    
    // ========== METHODS ==========
    
    /**
     * Subscribe to an event
     * 
     * Ví dụ:
     *   EventBus.subscribe(eventType -> {
     *       if (EventBus.EVENT_SERVICE_ORDER_CREATED.equals(eventType)) {
     *           System.out.println("Đơn dịch vụ được tạo!");
     *       }
     *   });
     */
    public static void subscribe(Consumer<String> listener) {
        if (listener != null) {
            listeners.add(listener);
            System.out.println("✓ EventBus: Listener registered. Total: " + listeners.size());
        }
    }
    
    /**
     * Publish an event - sẽ call tất cả listeners
     * 
     * Ví dụ:
     *   EventBus.publish(EventBus.EVENT_SERVICE_ORDER_CREATED);
     */
    public static void publish(String eventType) {
        if (eventType == null || eventType.isEmpty()) {
            System.err.println("⚠ EventBus: eventType không được null hoặc rỗng");
            return;
        }
        
        System.out.println("📢 EventBus: Publishing event '" + eventType + "' to " + listeners.size() + " listeners");
        
        for (Consumer<String> listener : new ArrayList<>(listeners)) {
            try {
                listener.accept(eventType);
            } catch (Exception e) {
                System.err.println("⚠ EventBus: Error in listener: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Unsubscribe
     */
    public static void unsubscribe(Consumer<String> listener) {
        if (listeners.remove(listener)) {
            System.out.println("✓ EventBus: Listener unregistered. Remaining: " + listeners.size());
        }
    }
    
    /**
     * Clear tất cả listeners (dùng khi close app)
     */
    public static void clearAll() {
        listeners.clear();
        System.out.println("✓ EventBus: All listeners cleared");
    }
    
    /**
     * Debug: in số listeners hiện tại
     */
    public static int getListenerCount() {
        return listeners.size();
    }
}