package com.salesmanager.test.shoppingcart;

import com.salesmanager.core.business.repositories.shoppingcart.ShoppingCartRepository;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Regression coverage for per-line merge decisions; persistence is isolated. */
public class ShoppingCartMergeTest {
    @Test
    public void mergesEveryMatchingProductInsteadOfOnlyTheFirst() throws Exception {
        assertMatchingLinesMerged(3);
    }

    @Test
    public void preservesTheSingleMatchingProductBehavior() throws Exception {
        assertMatchingLinesMerged(1);
    }

    private void assertMatchingLinesMerged(int count) throws Exception {
        ShoppingCartRepository repository = mock(ShoppingCartRepository.class);
        ProductService products = mock(ProductService.class);
        ShoppingCartServiceImpl service = spy(new ShoppingCartServiceImpl(repository));
        ReflectionTestUtils.setField(service, "productService", products);
        MerchantStore store = new MerchantStore();
        store.setId(1);
        ShoppingCart customer = new ShoppingCart();
        ShoppingCart guest = new ShoppingCart();
        List<ShoppingCartItem> originalLines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Product product = new Product();
            product.setId((long) i + 1);
            product.setSku("merge-product-" + i);
            product.setMerchantStore(store);
            ShoppingCartItem existing = new ShoppingCartItem(customer, product);
            existing.setQuantity(2);
            // Preserve the upstream rule that an existing attributed line is mergeable.
            existing.setAttributes(Collections.singleton(new ShoppingCartAttributeItem()));
            customer.getLineItems().add(existing);
            originalLines.add(existing);
            ShoppingCartItem incoming = new ShoppingCartItem(guest, product);
            incoming.setQuantity(3);
            guest.getLineItems().add(incoming);
            when(products.getBySku(product.getSku(), store, store.getDefaultLanguage())).thenReturn(product);
            doReturn(new ShoppingCartItem(product)).when(service).populateShoppingCartItem(product, store);
        }
        doNothing().when(service).saveOrUpdate(customer);
        doNothing().when(service).removeShoppingCart(guest);

        assertSame(customer, service.mergeShoppingCarts(customer, guest, store));

        assertEquals(count, customer.getLineItems().size());
        for (ShoppingCartItem line : originalLines) {
            assertEquals("Every matching line must retain the guest quantity", Integer.valueOf(5), line.getQuantity());
        }
        verify(service).saveOrUpdate(customer);
        verify(service).removeShoppingCart(guest);
    }
}
