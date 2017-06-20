package models;

import models.products.Book;
import models.products.Laptop;
import models.products.Product;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProductUtils {

    private ProductUtils() {}

    public static List<Product> sortedByPrice(List<Product> list) {
        return sorted(list, (p1, p2) -> new Double(p1.getPrice()).compareTo(p2.getPrice()));
    }

    public static List<Product> sortedByIdReversed(List<Product> list) {
        return sorted(list, (p1, p2) -> new Long(p2.getId()).compareTo(p1.getId()));
    }

    public static List<Product> filterBooks(List<Product> list) {
        return filter(list, (p) -> p instanceof Book);
    }

    public static List<Product> filterDualCoreLaptops(List<Product> list) {
        return filter(list, (p) -> p instanceof Laptop && ((Laptop)p).getCores() == 2);
    }

    private static List<Product> filter(List<Product> list, Predicate<Product> predicate) {
        return list
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private static List<Product> sorted(List<Product> list, Comparator<Product> comparator) {
        return list
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
