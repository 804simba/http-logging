package com.simba.quartz.web.model;

public class PaymentService {
    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("+1 (555) 555-5555");

        Vendor vendor = new Vendor();
        vendor.setId(2);
        vendor.setName("Vendor Name");
        vendor.setBusinessName("Vendor Business Name");

        initPayment(customer);
        initPayment(vendor);

    }

    public static void initPayment(Payer payer) {
        if (payer == null) {
            System.out.println("Payer cannot be null");
            return;
        }

        if (payer instanceof Customer customer) {
            System.out.println("Customer ID: " + customer.getId());
            System.out.println("Customer Name: " + customer.getName());
            System.out.println("Customer Email: " + customer.getEmail());
            System.out.println("Customer Phone: " + customer.getPhone());
        } else if (payer instanceof Vendor vendor) {
            System.out.println("Vendor ID: " + vendor.getId());
            System.out.println("Vendor Name: " + vendor.getName());
            System.out.println("Vendor Business Name: " + vendor.getBusinessName());
        }
    }
}
