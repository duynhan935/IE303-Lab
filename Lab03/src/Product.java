public class Product {
    private final String name;
    private final String brand;
    private final double price;
    private final String description;
    private final String imageName;   // e.g. "img1.png"  (inside assets/)

    public Product(String name, String brand, double price,
                   String description, String imageName) {
        this.name        = name;
        this.brand       = brand;
        this.price       = price;
        this.description = description;
        this.imageName   = imageName;
    }

    public String getName()            { return name; }
    public String getBrand()           { return brand; }
    public double getPrice()           { return price; }
    public String getDescription()     { return description; }
    public String getImageName()       { return imageName; }
    public String getFormattedPrice()  { return String.format("$%.2f", price); }
}
