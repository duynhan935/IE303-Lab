import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ShopApp {

    // ── Constants ─────────────────────────────────────────────────────────────
    private static final Color BG_WHITE   = new Color(0xFF, 0xFF, 0xFF);
    private static final Color BG_LIGHT   = new Color(0xF5, 0xF5, 0xF5);
    private static final Color BORDER_CLR = new Color(0xE0, 0xE0, 0xE0);
    private static final Color BLUE_SEL   = new Color(0x1A, 0x73, 0xE8);
    private static final Color TEXT_DARK  = new Color(0x11, 0x11, 0x11);
    private static final Color TEXT_GRAY  = new Color(0x77, 0x77, 0x77);
    private static final Color TEXT_MED   = new Color(0x55, 0x55, 0x55);
    private static final Font  FONT_BOLD  = new Font("Arial", Font.BOLD,  14);
    private static final Font  FONT_REG   = new Font("Arial", Font.PLAIN, 12);
    private static final Font  FONT_PRICE = new Font("Arial", Font.BOLD,  20);
    private static final Font  FONT_NAME  = new Font("Arial", Font.BOLD,  17);

    // ── Data ──────────────────────────────────────────────────────────────────
    private final List<Product> products = new ArrayList<>();

    // ── Left-pane shared controls ─────────────────────────────────────────────
    private JLabel mainImageLabel;
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel brandLabel;
    private JLabel descLabel;
    private JPanel leftPane;

    // ── Animation state ───────────────────────────────────────────────────────
    private Timer     fadeTimer;
    private float     fadeAlpha    = 1f;
    private Product   pendingProduct;
    private JPanel    selectedCard = null;

    // ═════════════════════════════════════════════════════════════════════════
    public void show() {
        initProducts();

        JFrame frame = new JFrame("Adidas Shop");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 660);
        frame.setLocationRelativeTo(null);
        frame.setBackground(BG_WHITE);

        // Root
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_WHITE);

        // ── LEFT ─────────────────────────────────────────────────────────────
        leftPane = buildLeftPane();

        // ── RIGHT scrollable grid ─────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(0, 4, 12, 12));
        grid.setBackground(BG_LIGHT);
        grid.setBorder(new EmptyBorder(20, 20, 20, 20));

        for (Product p : products) {
            grid.add(buildProductCard(p));
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_LIGHT);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(leftPane,    BorderLayout.WEST);
        root.add(scrollPane,  BorderLayout.CENTER);

        // Pre-select first product (no animation)
        if (!products.isEmpty()) {
            applyProductToLeft(products.get(0));
        }

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ─── Left detail pane ─────────────────────────────────────────────────────
    private JPanel buildLeftPane() {
        JPanel pane = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Paint with current fade alpha for animation
                ((Graphics2D) g).setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, fadeAlpha))));
            }
        };
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBackground(BG_WHITE);
        pane.setPreferredSize(new Dimension(310, 0));
        pane.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 1, BORDER_CLR),
                new EmptyBorder(40, 28, 40, 28)));

        // Product image
        mainImageLabel = new JLabel();
        mainImageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainImageLabel.setPreferredSize(new Dimension(254, 190));

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_CLR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        nameLabel  = makeLabel("", FONT_NAME, TEXT_DARK);
        priceLabel = makeLabel("", FONT_PRICE, TEXT_DARK);
        brandLabel = makeLabel("", FONT_REG,  TEXT_MED);
        descLabel  = makeLabel("", FONT_REG,  TEXT_GRAY);
        descLabel.setMaximumSize(new Dimension(254, Integer.MAX_VALUE));

        for (JLabel lbl : new JLabel[]{nameLabel, priceLabel, brandLabel, descLabel}) {
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        pane.add(mainImageLabel);
        pane.add(Box.createVerticalStrut(16));
        pane.add(sep);
        pane.add(Box.createVerticalStrut(14));
        pane.add(nameLabel);
        pane.add(Box.createVerticalStrut(4));
        pane.add(priceLabel);
        pane.add(Box.createVerticalStrut(4));
        pane.add(brandLabel);
        pane.add(Box.createVerticalStrut(8));
        pane.add(descLabel);

        return pane;
    }

    // ─── Product card for the right grid ──────────────────────────────────────
    private JPanel buildProductCard(Product product) {
        RoundPanel card = new RoundPanel(8);
        card.setLayout(new BorderLayout(0, 4));
        card.setBackground(BG_WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setNormalBorder();
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Top: name + description
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JLabel cardName = new JLabel(truncate(product.getName(), 20));
        cardName.setFont(new Font("Arial", Font.BOLD, 15));
        cardName.setForeground(TEXT_DARK);

        JLabel cardDesc = new JLabel(truncate(product.getDescription(), 28));
        cardDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        cardDesc.setForeground(TEXT_GRAY);

        topPanel.add(cardName);
        topPanel.add(cardDesc);

        // Middle: thumbnail image
        JLabel thumbLabel = new JLabel();
        thumbLabel.setHorizontalAlignment(SwingConstants.CENTER);
        thumbLabel.setPreferredSize(new Dimension(140, 100));
        loadScaledImage(thumbLabel, product.getImageName(), 140, 100);

        // Bottom: brand + price
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        JLabel cardBrand = new JLabel(product.getBrand());
        cardBrand.setFont(new Font("Arial", Font.PLAIN, 12));
        cardBrand.setForeground(TEXT_MED);

        JLabel cardPrice = new JLabel(product.getFormattedPrice());
        cardPrice.setFont(new Font("Arial", Font.BOLD, 16));
        cardPrice.setForeground(TEXT_DARK);

        bottomRow.add(cardBrand, BorderLayout.WEST);
        bottomRow.add(cardPrice, BorderLayout.EAST);

        card.add(topPanel,   BorderLayout.NORTH);
        card.add(thumbLabel, BorderLayout.CENTER);
        card.add(bottomRow,  BorderLayout.SOUTH);

        // ── Hover ────────────────────────────────────────────────────────────
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (card != selectedCard) card.setHoverBorder();
            }
            @Override public void mouseExited(MouseEvent e) {
                if (card != selectedCard) card.setNormalBorder();
            }

            // ── Click → select + update left pane with fade ───────────────
            @Override public void mouseClicked(MouseEvent e) {
                if (selectedCard != null) ((RoundPanel) selectedCard).setNormalBorder();
                selectedCard = card;
                card.setSelectedBorder();

                // Kick off fade transition
                startFade(product);
            }
        });

        return card;
    }

    // ─── Fade-out → swap data → fade-in ──────────────────────────────────────
    private void startFade(Product product) {
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();

        pendingProduct = product;
        fadeAlpha      = 1f;

        // Phase 1: fade out (alpha 1→0)
        fadeTimer = new Timer(16, null);
        fadeTimer.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                fadeAlpha -= 0.08f;
                if (fadeAlpha <= 0f) {
                    fadeAlpha = 0f;
                    fadeTimer.stop();
                    // Swap content
                    applyProductToLeft(pendingProduct);
                    // Phase 2: fade in (alpha 0→1)
                    startFadeIn();
                }
                leftPane.repaint();
            }
        });
        fadeTimer.start();
    }

    private void startFadeIn() {
        fadeTimer = new Timer(16, null);
        fadeTimer.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                fadeAlpha += 0.08f;
                if (fadeAlpha >= 1f) {
                    fadeAlpha = 1f;
                    fadeTimer.stop();
                }
                leftPane.repaint();
            }
        });
        fadeTimer.start();
    }

    // ─── Apply product data to left pane ─────────────────────────────────────
    private void applyProductToLeft(Product p) {
        loadScaledImage(mainImageLabel, p.getImageName(), 254, 190);
        nameLabel.setText(p.getName());
        priceLabel.setText(p.getFormattedPrice());
        brandLabel.setText(p.getBrand());
        descLabel.setText("<html><body style='width:220px'>" + p.getDescription() + "</body></html>");
        leftPane.revalidate();
    }

    // ─── Load and scale an image into a JLabel ────────────────────────────────
    private void loadScaledImage(JLabel label, String imageName, int w, int h) {
        try {
            File f = new File("assets/" + imageName);
            if (!f.exists()) f = new File("assets\\" + imageName);
            if (!f.exists()) f = new File("Lab03/assets/" + imageName);
            if (!f.exists()) f = new File("Lab03\\assets\\" + imageName);
            BufferedImage raw;
            if (f.exists()) {
                raw = ImageIO.read(f);
            } else {
                // Try classpath
                var is = getClass().getResourceAsStream("/assets/" + imageName);
                if (is == null) is = getClass().getResourceAsStream("/" + imageName);
                if (is == null) { label.setIcon(null); return; }
                raw = ImageIO.read(is);
            }
            if (raw == null) { label.setIcon(null); return; }

            // Scale keeping aspect ratio
            double sx = (double) w / raw.getWidth();
            double sy = (double) h / raw.getHeight();
            double scale = Math.min(sx, sy);
            int tw = (int) (raw.getWidth()  * scale);
            int th = (int) (raw.getHeight() * scale);

            Image scaled = raw.getScaledInstance(tw, th, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
            label.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception ex) {
            System.err.println("Cannot load: " + imageName + " – " + ex.getMessage());
        }
    }

    // ─── Rounded-corner JPanel ────────────────────────────────────────────────
    static class RoundPanel extends JPanel {
        private final int arc;
        private Color borderColor = new Color(0xE0, 0xE0, 0xE0);
        private float borderWidth = 1f;

        RoundPanel(int arc) { this.arc = arc; setOpaque(false); }

        void setNormalBorder()   { borderColor = new Color(0xE0,0xE0,0xE0); borderWidth=1f; repaint(); }
        void setHoverBorder()    { borderColor = new Color(0xBB,0xBB,0xBB); borderWidth=1.5f; repaint(); }
        void setSelectedBorder() { borderColor = new Color(0x1A,0x73,0xE8); borderWidth=2f; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Background
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
            // Border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.draw(new RoundRectangle2D.Float(
                    borderWidth/2, borderWidth/2,
                    getWidth()-borderWidth, getHeight()-borderWidth, arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private static JLabel makeLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    private static String truncate(String s, int maxLen) {
        return s.length() > maxLen ? s.substring(0, maxLen - 1) + "…" : s;
    }

    // ─── Product data ─────────────────────────────────────────────────────────
    private void initProducts() {
        products.add(new Product("4DFWD PULSE SHOES",  "Adidas", 160.00,
                "This product is excluded from all promotional discounts and offers.", "img1.png"));
        products.add(new Product("FORUM MID SHOES",    "Adidas", 100.00,
                "This product is excluded from all promotional discounts and offers.", "img2.png"));
        products.add(new Product("SUPERNOVA SHOES",    "Adidas", 150.00,
                "NMD City Stock 2", "img3.png"));
        products.add(new Product("Adidas",             "Adidas", 160.00,
                "NMD City Stock 2", "img4.png"));
        products.add(new Product("Adidas",             "Adidas", 120.00,
                "NMD City Stock 2", "img5.png"));
        products.add(new Product("4DFWD PULSE SHOES",  "Adidas", 160.00,
                "This product is excluded from all promotional discounts and offers.", "img6.png"));
        products.add(new Product("4DFWD PULSE SHOES",  "Adidas", 160.00,
                "This product is excluded from all promotional discounts and offers.", "img1.png"));
        products.add(new Product("FORUM MID SHOES",    "Adidas", 100.00,
                "This product is excluded from all promotional discounts and offers.", "img2.png"));
    }
}
