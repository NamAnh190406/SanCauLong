package Controller;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;


public class KhungMo extends javax.swing.JPanel {
    public KhungMo(){
        setOpaque(false);}         /*set mặc định hay không*/
        
    @Override
        protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();    /*ép kiểu từ Graphic sang Graphic2D*/
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); /*đường viền bo góc*/

        g2.setColor(new Color(255, 255, 255, 100));  /*255 *3 là màu trắng*, 100 là độ trong*/
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));    /*tạo ô*/

        g2.setColor(new Color(255, 255, 255, 150));
        g2.setStroke(new BasicStroke(1.5f));    /*tạo viền với độ đậm là 1.5*/
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 30, 30));

        g2.dispose();
    }
}

/*dể set mặc định ở ngoài override vì phần ngoài thay đổi những cái mặc định, tỏng override vẽ thêm*/
