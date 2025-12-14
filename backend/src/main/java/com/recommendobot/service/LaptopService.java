package com.recommendobot.service;

import com.recommendobot.model.Laptop;
import com.recommendobot.model.LaptopResponse;
import com.recommendobot.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * LaptopService - Business logic for laptop operations
 */
@Service
public class LaptopService {

    @Autowired
    private LaptopRepository laptopRepository;

    @Autowired
    private CsvSyncService csvSyncService;

    public List<LaptopResponse> getAllLaptops() {
        return laptopRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public LaptopResponse getLaptopById(Long id) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laptop not found with id: " + id));
        return convertToResponse(laptop);
    }

    public List<String> getAllBrands() {
        return laptopRepository.findAllDistinctBrands();
    }

    public List<String> getAllMemoryOptions() {
        return laptopRepository.findAllDistinctMemoryOptions();
    }

    public List<String> getAllStorageOptions() {
        return laptopRepository.findAllDistinctStorageOptions();
    }

    public List<String> getAllDisplaySizes() {
        return laptopRepository.findAllDistinctDisplaySizes();
    }

    public LaptopResponse createLaptop(Laptop laptop) {
        Laptop savedLaptop = laptopRepository.save(laptop);
        csvSyncService.syncDatabaseToCsv();
        return convertToResponse(savedLaptop);
    }

    public LaptopResponse updateLaptop(Long id, Laptop laptopDetails) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laptop not found with id: " + id));

        laptop.setBrandName(laptopDetails.getBrandName());
        laptop.setProduct(laptopDetails.getProduct());
        laptop.setPrice(laptopDetails.getPrice());
        laptop.setProcessor(laptopDetails.getProcessor());
        laptop.setMemory(laptopDetails.getMemory());
        laptop.setStorage(laptopDetails.getStorage());
        laptop.setGraphics(laptopDetails.getGraphics());
        laptop.setDisplay(laptopDetails.getDisplay());
        laptop.setOs(laptopDetails.getOs());
        laptop.setImageUrl(laptopDetails.getImageUrl());

        Laptop updatedLaptop = laptopRepository.save(laptop);
        csvSyncService.syncDatabaseToCsv();
        
        return convertToResponse(updatedLaptop);
    }

    public void deleteLaptop(Long id) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laptop not found with id: " + id));
        
        laptopRepository.delete(laptop);
        csvSyncService.syncDatabaseToCsv();
    }

    private LaptopResponse convertToResponse(Laptop laptop) {
        LaptopResponse response = new LaptopResponse();
        response.setId(laptop.getId());
        response.setBrandName(laptop.getBrandName());
        response.setProduct(laptop.getProduct());
        response.setPrice(formatPrice(laptop.getPrice()));
        response.setProcessor(laptop.getProcessor());
        response.setMemory(laptop.getMemory());
        response.setStorage(laptop.getStorage());
        response.setGraphics(laptop.getGraphics());
        response.setDisplay(laptop.getDisplay());
        response.setOs(laptop.getOs());
        response.setImageUrl(laptop.getImageUrl());
        return response;
    }

    // --- New search and helper methods used by controllers/frontend ---

    public static class SearchRequestDto {
        public String query;
        public Filters filters;
        public String sortBy;
        public Integer page = 1;
        public Integer pageSize = 12;

        public static class Filters {
            public java.util.List<String> brands;
            public java.util.List<String> ram;
            public java.util.List<String> storage;
            public java.util.List<String> display;
            public java.util.List<String> graphics;
            public java.util.List<java.math.BigDecimal> priceRange;
        }
    }

    public static class SearchResponseDto {
        public java.util.List<LaptopResponse> laptops;
        public Pagination pagination;

        public static class Pagination {
            public int currentPage;
            public int totalPages;
            public long totalItems;
            public int itemsPerPage;
            public boolean hasNextPage;
            public boolean hasPrevPage;
        }
    }

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public SearchResponseDto searchLaptops(SearchRequestDto request) {
        int page = (request.page == null || request.page < 1) ? 1 : request.page;
        int pageSize = (request.pageSize == null || request.pageSize < 1) ? 12 : request.pageSize;

        // Build criteria query
    jakarta.persistence.criteria.CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    jakarta.persistence.criteria.CriteriaQuery<Laptop> cq = cb.createQuery(Laptop.class);
    jakarta.persistence.criteria.Root<Laptop> root = cq.from(Laptop.class);

    java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

        if (request.query != null && !request.query.trim().isEmpty()) {
            String like = "%" + request.query.trim().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("brandName")), like),
                    cb.like(cb.lower(root.get("product")), like),
                    cb.like(cb.lower(root.get("processor")), like),
                    cb.like(cb.lower(root.get("graphics")), like),
                    cb.like(cb.lower(root.get("os")), like)
            ));
        }

        if (request.filters != null) {
            if (request.filters.brands != null && !request.filters.brands.isEmpty()) {
                predicates.add(root.get("brandName").in(request.filters.brands));
            }
            if (request.filters.ram != null && !request.filters.ram.isEmpty()) {
                predicates.add(root.get("memory").in(request.filters.ram));
            }
            if (request.filters.storage != null && !request.filters.storage.isEmpty()) {
                predicates.add(root.get("storage").in(request.filters.storage));
            }
            if (request.filters.display != null && !request.filters.display.isEmpty()) {
                predicates.add(root.get("display").in(request.filters.display));
            }
            if (request.filters.graphics != null && !request.filters.graphics.isEmpty()) {
                predicates.add(root.get("graphics").in(request.filters.graphics));
            }
            if (request.filters.priceRange != null && request.filters.priceRange.size() >= 2) {
                java.math.BigDecimal min = request.filters.priceRange.get(0);
                java.math.BigDecimal max = request.filters.priceRange.get(1);
                predicates.add(cb.between(root.get("price"), min, max));
            }
        }

    cq.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));

        // Sorting
        if ("price_desc".equalsIgnoreCase(request.sortBy)) {
            cq.orderBy(cb.desc(root.get("price")));
        } else if ("price_asc".equalsIgnoreCase(request.sortBy)) {
            cq.orderBy(cb.asc(root.get("price")));
        } else {
            cq.orderBy(cb.asc(root.get("id"))); // default
        }

    jakarta.persistence.TypedQuery<Laptop> typedQuery = entityManager.createQuery(cq);

        // Count total
    jakarta.persistence.criteria.CriteriaQuery<Long> countCQ = cb.createQuery(Long.class);
    jakarta.persistence.criteria.Root<Laptop> countRoot = countCQ.from(Laptop.class);
        countCQ.select(cb.count(countRoot));
    java.util.List<jakarta.persistence.criteria.Predicate> countPreds = new java.util.ArrayList<>();
        // replicate predicates for count
        if (request.query != null && !request.query.trim().isEmpty()) {
            String like = "%" + request.query.trim().toLowerCase() + "%";
            countPreds.add(cb.or(
                    cb.like(cb.lower(countRoot.get("brandName")), like),
                    cb.like(cb.lower(countRoot.get("product")), like),
                    cb.like(cb.lower(countRoot.get("processor")), like),
                    cb.like(cb.lower(countRoot.get("graphics")), like),
                    cb.like(cb.lower(countRoot.get("os")), like)
            ));
        }
        if (request.filters != null) {
            if (request.filters.brands != null && !request.filters.brands.isEmpty()) {
                countPreds.add(countRoot.get("brandName").in(request.filters.brands));
            }
            if (request.filters.ram != null && !request.filters.ram.isEmpty()) {
                countPreds.add(countRoot.get("memory").in(request.filters.ram));
            }
            if (request.filters.storage != null && !request.filters.storage.isEmpty()) {
                countPreds.add(countRoot.get("storage").in(request.filters.storage));
            }
            if (request.filters.display != null && !request.filters.display.isEmpty()) {
                countPreds.add(countRoot.get("display").in(request.filters.display));
            }
            if (request.filters.graphics != null && !request.filters.graphics.isEmpty()) {
                countPreds.add(countRoot.get("graphics").in(request.filters.graphics));
            }
            if (request.filters.priceRange != null && request.filters.priceRange.size() >= 2) {
                java.math.BigDecimal min = request.filters.priceRange.get(0);
                java.math.BigDecimal max = request.filters.priceRange.get(1);
                countPreds.add(cb.between(countRoot.get("price"), min, max));
            }
        }
    countCQ.where(countPreds.toArray(new jakarta.persistence.criteria.Predicate[0]));
    Long totalItems = entityManager.createQuery(countCQ).getSingleResult();

        // Pagination
        int firstResult = (page - 1) * pageSize;
        typedQuery.setFirstResult(firstResult);
        typedQuery.setMaxResults(pageSize);
        java.util.List<Laptop> results = typedQuery.getResultList();

        SearchResponseDto resp = new SearchResponseDto();
        resp.laptops = results.stream().map(this::convertToResponse).collect(java.util.stream.Collectors.toList());

        SearchResponseDto.Pagination p = new SearchResponseDto.Pagination();
        p.currentPage = page;
        p.itemsPerPage = pageSize;
        p.totalItems = totalItems;
        p.totalPages = (int) Math.ceil((double) totalItems / pageSize);
        p.hasNextPage = page < p.totalPages;
        p.hasPrevPage = page > 1;
        resp.pagination = p;

        return resp;
    }

    public java.util.List<String> getAutocompleteSuggestions(String partial) {
        if (partial == null || partial.trim().isEmpty()) return java.util.Collections.emptyList();
        String like = "%" + partial.trim().toLowerCase() + "%";
        // Query distinct brandName and product
    jakarta.persistence.Query q1 = entityManager.createQuery("SELECT DISTINCT lower(l.brandName) FROM Laptop l WHERE lower(l.brandName) LIKE :like", String.class);
        q1.setParameter("like", like);
    jakarta.persistence.Query q2 = entityManager.createQuery("SELECT DISTINCT lower(l.product) FROM Laptop l WHERE lower(l.product) LIKE :like", String.class);
        q2.setParameter("like", like);

        java.util.Set<String> combined = new java.util.LinkedHashSet<>();
        ((java.util.List<String>)q1.getResultList()).forEach(s -> combined.add(s));
        ((java.util.List<String>)q2.getResultList()).forEach(s -> combined.add(s));

        return combined.stream().map(s -> {
            // return original-case versions when possible
            return s;
        }).limit(10).collect(java.util.stream.Collectors.toList());
    }

    public String spellCheckQuery(String query) {
        if (query == null || query.trim().isEmpty()) return query;
        // Exact match on product
        java.util.List<Laptop> exact = laptopRepository.findByProductContainingIgnoreCase(query.trim());
        if (exact != null && !exact.isEmpty()) {
            // return exact first product match
            return exact.get(0).getProduct();
        }
        // fallback: find similar by LIKE
        java.util.List<Laptop> likeMatches = laptopRepository.findByProductContainingIgnoreCase(query.trim());
        if (likeMatches != null && !likeMatches.isEmpty()) return likeMatches.get(0).getProduct();
        return query; // no suggestion
    }

    private String formatPrice(BigDecimal price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(price);
    }
}
