package org.example.service.db.orderStorage;

import org.example.config.ConfigReader;
import org.example.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XmlOrderStorageServiceImplTest {
    private XmlOrderStorageServiceImpl xmlOrderStorageService;
    private ConfigReader mockConfigReader;
    private String mockOrdersDirectory;

    @BeforeEach
    void setUp() {

        mockConfigReader = mock(ConfigReader.class);
        mockOrdersDirectory = "mockDirectory";
        when(mockConfigReader.getOrdersDirectory()).thenReturn(mockOrdersDirectory);

    }

    @Test
    void should_return_success_message_when_orders_are_added_to_xml() throws Exception {
        // given
        Order order1 = new Order("Order 1", "2024-05-01", 100.0, 450.0);
        Order order2 = new Order("Order 2", "2024-06-01", 120.0, 540.0);
        List<Order> orders = Arrays.asList(order1, order2);

        File mockDirectory = mock(File.class);
        when(mockDirectory.exists()).thenReturn(false);
        when(mockDirectory.mkdirs()).thenReturn(true);
        xmlOrderStorageService = new XmlOrderStorageServiceImpl();
        // when
        String result = xmlOrderStorageService.addOrders(orders);

        // then
        assertThat(result).contains("Utworzono plik z 2 zamówieniami");
    }

    @Test
    void should_add_orders_to_existing_xml_file() throws Exception {
        // given
        Order order1 = new Order("Order 1", "2024-05-01", 100.0, 450.0);
        Order order2 = new Order("Order 2", "2024-06-01", 120.0, 540.0);
        List<Order> orders = Arrays.asList(order1, order2);

        File mockXmlFile = mock(File.class);
        when(mockXmlFile.exists()).thenReturn(true);

        File mockDirectory = mock(File.class);
        when(mockDirectory.exists()).thenReturn(false);
        when(mockDirectory.mkdirs()).thenReturn(true);

        xmlOrderStorageService = new XmlOrderStorageServiceImpl();

        // when
        String result = xmlOrderStorageService.addOrders(orders);

        // then
        assertThat(result).contains("Utworzono plik z 2 zamówieniami");
    }


}