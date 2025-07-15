package com.cargo.services;

import com.cargo.models.dtos.ProductDto;
import com.cargo.models.entities.Product; // entities paketini doğruca ekledik
import com.cargo.repository.ProductRepository;
import com.cargo.models.mappers.ProductMapper; // mapper paketini doğruca ekledik
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException; // Product bulunamadığında kullanılacak
import java.util.Optional; // getById metodu Optional dönebilir

@Service
@Transactional // Servis katmanındaki metotlar için genellikle Transactional kullanılır
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Tüm aktif ve stokta olan ürünleri döndürür.
     * @return Aktif ve stokta olan ProductDto listesi.
     */
    public List<ProductDto> getActiveProductsInStock() {
        // productRepository.findByActiveTrueAndStockGreaterThan(0) metodunun repository'de tanımlı olması gerekir.
        // Bu metod, hem aktif olan hem de stok adeti 0'dan büyük olan ürünleri getirecektir.
        return productMapper.toDtoList(productRepository.findByActiveTrueAndStockGreaterThan(0));
    }

    /**
     * Anahtar kelimeye göre aktif ürünleri arar.
     * @param keyword Aranacak kelime.
     * @return Arama kriterlerine uyan aktif ProductDto listesi.
     */
    public List<ProductDto> searchActiveProducts(String keyword) {
        // productRepository.findByActiveTrueAndNameContainingIgnoreCase(keyword) metodunun repository'de tanımlı olması gerekir.
        // Arama yaparken genellikle büyük/küçük harf duyarsızlığı istenir, bu yüzden 'IgnoreCase' ekledim.
        // Eğer service katmanı çağrısı 'NameContaining' ise, repository'deki method ismini de buna uygun yapmalıyız.
        // Ancak tavsiye edilen 'IgnoreCase' kullanmaktır.
        return productMapper.toDtoList(productRepository.findByActiveTrueAndNameContainingIgnoreCase(keyword));
    }

    /**
     * Belirli bir ID'ye sahip ürünü döndürür.
     * @param id Ürün ID'si.
     * @return Ürünü içeren Optional<Product> (bulunamazsa boş Optional).
     */
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Belirli bir ID ve adette ürünün mevcut olup olmadığını kontrol eder.
     * Hem aktif olmalı hem de yeterli stok bulunmalıdır.
     * @param productId Ürün ID'si.
     * @param quantity Kontrol edilecek adet.
     * @return Ürün mevcut ve stok yeterliyse true, aksi halde false.
     */
    public boolean isAvailable(Long productId, int quantity) {
        return productRepository.findById(productId)
                .map(product -> product.isActive() && product.getStock() >= quantity)
                .orElse(false); // Ürün bulunamazsa veya pasifse false döndür
    }

    /**
     * Ürünün stoğunu azaltır.
     * @param productId Stoğu azaltılacak ürünün ID'si.
     * @param quantity Azaltılacak adet.
     * @throws NoSuchElementException Ürün bulunamazsa.
     * @throws IllegalStateException Stok yetersizse.
     */
    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Ürün bulunamadı: " + productId));

        if (product.getStock() < quantity) {
            throw new IllegalStateException("Yetersiz stok! Ürün: " + product.getName() + ", Mevcut Stok: " + product.getStock() + ", İstenen: " + quantity);
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    /**
     * Ürünün stoğunu artırır (örn. sipariş iptallerinde).
     * @param productId Stoğu artırılacak ürünün ID'si.
     * @param quantity Artırılacak adet.
     * @throws NoSuchElementException Ürün bulunamazsa.
     */
    public void increaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Ürün bulunamadı: " + productId));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    /**
     * Yeni bir ürün kaydeder veya mevcut bir ürünü günceller.
     * @param productDto Kaydedilecek/güncellenecek ürünün DTO'su.
     * @return Kaydedilen/güncellenen ürünün DTO'su.
     */
    public ProductDto saveProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        // Yeni bir ürünse 'active' ve 'createdAt' gibi alanları burada set edebilirsiniz.
        if (product.getId() == null) {
            product.setActive(true); // Yeni ürün varsayılan olarak aktif
            // product.setCreatedAt(new Date()); // Oluşturulma tarihi
        }
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    /**
     * Belirtilen ID'ye sahip ürünü siler.
     * Not: Genellikle ürünler silinmez, pasif hale getirilir (soft delete).
     * @param id Silinecek ürünün ID'si.
     */
    @Transactional // Bu işlem de transactional olmalı
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Belirtilen ID'ye sahip ürünü pasif hale getirir (soft delete).
     * @param id Pasif hale getirilecek ürünün ID'si.
     * @throws NoSuchElementException Ürün bulunamazsa.
     */
    public ProductDto deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ürün bulunamadı: " + id));
        product.setActive(false);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }
    /**
     * Belirli bir ID'ye sahip ürün entity'sini döndürür.
     * @param id Ürün ID'si
     * @return Ürün entity'sini içeren Optional<Product>
     */
    public Optional<Product> getProductEntityById(Long id) {
        return productRepository.findById(id);
    }
}