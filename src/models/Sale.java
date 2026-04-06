package models;

import java.time.LocalDate;

public class Sale {
    private int id;
    private int client_id;
    private int medication_id;
    private LocalDate sale_date;
    private int quantity;
    private double total_price;
    private String payment_method;

    public Sale() {};

    public Sale
                (LocalDate sale_date,
                int quantity, double total_price, String paymentMethod) {
                    this.quantity = quantity;
                    this.sale_date = sale_date;
                    this.quantity = quantity;
                    this.total_price = total_price;
                    this.payment_method = payment_method;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return client_id; }
    public void setClientId(int client_id) {
        if (client_id <= 0) {
            throw new IllegalArgumentException("Invalid client.");
        }
        this.client_id = client_id;
    }

    public int getMedicationId() { return medication_id; }
    public void setMedicationId(int medication_id) {
        if (medication_id <= 0) {
            throw new IllegalArgumentException("Invalid medicine.");
        }
        this.medication_id = medication_id;
    }

    public LocalDate getSaleDate() { return sale_date; }
    public void setSaleDate(LocalDate sale_date) { this.sale_date = sale_date; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return total_price; }
    public void setTotalPrice(double total_price) { this.total_price = total_price; }

    public String getPaymentMethod() { return payment_method; }
    public void setPaymentMethod(String payment_method) {
        if (payment_method != null && payment_method.length() > 20) {
            throw new IllegalArgumentException("Can't exceed 20 characters.");
        }
        this.payment_method = payment_method;
    }
    @Override
    public String toString() {
        return "Sale #" + id + " | Client ID: " + client_id +
               " | Medicine ID: " + medication_id + " | Quantity: " +
               quantity + " | " + total_price + " €. | " + sale_date;
    }
}
