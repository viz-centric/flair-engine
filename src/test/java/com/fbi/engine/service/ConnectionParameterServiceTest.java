package com.fbi.engine.service;

import com.fbi.engine.FbiengineApp;
import com.fbi.engine.domain.ConnectionParameter;
import com.fbi.engine.repository.ConnectionParameterRepository;
import com.fbi.engine.service.dto.ConnectionParameters;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FbiengineApp.class)
@Transactional
public class ConnectionParameterServiceTest {

    @Autowired
    private ConnectionParameterService connectionParameterService;

    @Autowired
    private ConnectionParameterRepository connectionParameterRepository;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void save() {
        connectionParameterService.save("1234", ImmutableMap.of("param1", "value1", "param2", "value2"));
        List<ConnectionParameter> params = connectionParameterRepository.findAllByLinkId("1234");

        assertEquals(2, params.size());
        ConnectionParameter p1 = params.stream()
                .filter(i -> i.getName().equals("param1"))
                .findFirst()
                .get();
        assertEquals("value1", p1.getValue());

        ConnectionParameter p2 = params.stream()
                .filter(i -> i.getName().equals("param2"))
                .findFirst()
                .get();
        assertEquals("value2", p2.getValue());

        connectionParameterService.save("1234", ImmutableMap.of("param1", "value1", "param3", "value3"));
        params = connectionParameterRepository.findAllByLinkId("1234");

        assertEquals(2, params.size());
        p1 = params.stream()
                .filter(i -> i.getName().equals("param1"))
                .findFirst()
                .get();
        assertEquals("value1", p1.getValue());

        p2 = params.stream()
                .filter(i -> i.getName().equals("param3"))
                .findFirst()
                .get();
        assertEquals("value3", p2.getValue());
    }

    @Test
    public void getParametersByLinkId() {
        ConnectionParameter p1 = new ConnectionParameter();
        p1.setName("param1");
        p1.setValue("val1");
        p1.setLinkId("1234");

        ConnectionParameter p2 = new ConnectionParameter();
        p2.setName("param2");
        p2.setValue("val2");
        p2.setLinkId("1234");

        ConnectionParameter p3 = new ConnectionParameter();
        p3.setName("param2");
        p3.setValue("val2");
        p3.setLinkId("1234");
        connectionParameterRepository.saveAll(Arrays.asList(p1, p2, p3));

        Map<String, String> params = connectionParameterService.getParametersByLinkId("1234");
        assertEquals(2, params.size());
        assertEquals("val1", params.get("param1"));
        assertEquals("val2", params.get("param2"));
    }

    @Test
    public void getParameters() {
        ConnectionParameter p1 = new ConnectionParameter();
        p1.setName("cacheEnabled");
        p1.setValue("true");
        p1.setLinkId("1234");

        ConnectionParameter p2 = new ConnectionParameter();
        p2.setName("cachePurgeAfterMinutes");
        p2.setValue("1");
        p2.setLinkId("1234");

        ConnectionParameter p3 = new ConnectionParameter();
        p3.setName("refreshAfterTimesRead");
        p3.setValue("2");
        p3.setLinkId("1234");

        ConnectionParameter p4 = new ConnectionParameter();
        p4.setName("refreshAfterMinutes");
        p4.setValue("3");
        p4.setLinkId("1234");

        ConnectionParameter p5 = new ConnectionParameter();
        p5.setName("refreshAfterMinutes");
        p5.setValue("4");
        p5.setLinkId("1234");
        connectionParameterRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        ConnectionParameters parameters = connectionParameterService.getParameters("1234");
        assertTrue(parameters.isEnabled());
        assertEquals(1, parameters.getCachePurgeAfterMinutes());
        assertEquals(2, parameters.getRefreshAfterTimesRead());
        assertEquals(3, parameters.getRefreshAfterMinutes());

    }

    @Test
    public void deleteByLinkId() {
        ConnectionParameter p1 = new ConnectionParameter();
        p1.setName("cacheEnabled");
        p1.setValue("true");
        p1.setLinkId("1234");

        ConnectionParameter p2 = new ConnectionParameter();
        p2.setName("cachePurgeAfterMinutes");
        p2.setValue("1");
        p2.setLinkId("1234");

        ConnectionParameter p3 = new ConnectionParameter();
        p3.setName("refreshAfterTimesRead");
        p3.setValue("2");
        p3.setLinkId("1234");

        ConnectionParameter p4 = new ConnectionParameter();
        p4.setName("refreshAfterMinutes");
        p4.setValue("3");
        p4.setLinkId("1234");

        ConnectionParameter p5 = new ConnectionParameter();
        p5.setName("refreshAfterMinutes");
        p5.setValue("4");
        p5.setLinkId("1234");
        connectionParameterRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        connectionParameterService.deleteByLinkId("1234");

        List<ConnectionParameter> params = connectionParameterRepository.findAllByLinkId("1234");
        assertEquals(0, params.size());
    }
}
