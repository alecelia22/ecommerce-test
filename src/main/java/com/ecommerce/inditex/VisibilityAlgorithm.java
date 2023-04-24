package com.ecommerce.inditex;

import com.ecommerce.inditex.domain.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VisibilityAlgorithm {

    public static void main(String[] args) {
        List<String[]> products = readCsvFile("product.csv");
        List<String[]> sizes = readCsvFile("size.csv");
        Map<String, Integer> stockMap = createStockMap();

        // List of products to print
        List<Product> visibleProducts = new ArrayList<>();

        for (String[] product : products) {
            String productId = product[0];
            if (productIsVisible(productId, sizes, stockMap)) {
                visibleProducts.add(newProduct(product));
            }
        }

        // Sort products by sequence
        visibleProducts.sort(Comparator.comparingInt(Product::getSequence));

        System.out.println(String.join(",", visibleProducts.stream().map(p -> String.valueOf(p.getId())).toList()));
    }

    private static Map<String, Integer> createStockMap() {
        Map<String, Integer> stockMap = new HashMap<>();
        URL url = Thread.currentThread().getContextClassLoader().getResource("stock.csv");

        if (url != null) {
            try (Scanner scanner = new Scanner(new File(url.getFile()))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] fields = line.replace(" ", "").split(",");
                    stockMap.put(fields[0], Integer.parseInt(fields[1]));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return stockMap;
    }

    private static boolean productIsVisible(String productId, List<String[]> sizes, Map<String, Integer> stockMap) {
        boolean isVisible = false;

        for (String[] size : sizes) {
            if (size[1].equals(productId)) {
                boolean isBackSoon = Boolean.parseBoolean(size[2]);
                boolean isSpecial = Boolean.parseBoolean(size[3]);
                String sizeId = size[0];

                // Check if this size has available stock
                if (stockMap.containsKey(sizeId) && stockMap.get(sizeId) > 0 && !isSpecial
                    || isBackSoon && !isSpecial) {
                    isVisible = true;
                    break;
                }
            }
        }

        return isVisible;
    }


    private static Product newProduct(String[] product) {
        return Product.builder()
                .id(Long.parseLong(product[0]))
                .sequence(Integer.parseInt(product[1]))
                .build();
    }

    private static List<String[]> readCsvFile(String filename) {
        List<String[]> rows = new ArrayList<>();
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);

        if (url != null) {
            try (Scanner scanner = new Scanner(new File(url.getFile()))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] fields = line.replace(" ", "").split(",");
                    rows.add(fields);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return rows;
    }
}

