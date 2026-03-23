package models;

public class Category {
    private int id;
    private String category_name;
    private String description;

    public Category() {}

    public Category(String category_name, String description) {
        this.category_name = category_name;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategoryName() { return category_name; }
    public void setCategoryName(String category_name)
            {   if(category_name == null || category_name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name can't be empty.");}
                if(category_name.length() > 50) {
                throw new IllegalArgumentException
                ("Name can't be longer than 50 characters");
                }
                this.category_name = category_name.trim(); }

    public String getDescription() { return description; }
    public void setDescription()
                {if(description != null && description.length() > 200) {
                throw new IllegalArgumentException
                ("Description can't exceed 200 characters");}
                this.description = description; }

    @Override
    public String toString() {
        return category_name;}
}
