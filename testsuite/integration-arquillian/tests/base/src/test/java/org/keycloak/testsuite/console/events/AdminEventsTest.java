package org.keycloak.testsuite.console.events;

import org.jboss.arquillian.graphene.page.Page;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.clients.AbstractClientTest;
import org.keycloak.testsuite.console.page.clients.Clients;
import org.keycloak.testsuite.console.page.events.AdminEvents;
import org.keycloak.testsuite.console.page.events.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import java.util.List;


/**
 * @author mhajas
 */
public class AdminEventsTest extends AbstractConsoleTest {

    @Page
    private AdminEvents adminEventsPage;

    @Page
    private Config configPage;

    @Page
    private Clients clientsPage;

    private ClientRepresentation newClient;

    @Before
    public void beforeAdminEventsTest() {
        configPage.navigateTo();
        configPage.form().setSaveAdminEvents(true);
        configPage.form().setIncludeRepresentation(true);
        configPage.form().save();
    }

    @Test
    public void clientsAdminEventsTest() {
        newClient = AbstractClientTest.createClientRepresentation("test_client", "http://example.test/test_client/*");
        Response response = clientsPage.clientsResource().create(newClient);
        String id = ApiUtil.getCreatedId(response);
        response.close();
        newClient.setClientId("test_client2");
        clientsPage.clientsResource().get(id).update(newClient);
        clientsPage.clientsResource().get(id).remove();

        adminEventsPage.navigateTo();
        adminEventsPage.table().filter();
        adminEventsPage.table().filterForm().addOperationType("CREATE");
        adminEventsPage.table().update();

        List<WebElement> resultList = adminEventsPage.table().rows();
        assertEquals(1, resultList.size());
        resultList.get(0).findElement(By.xpath("//td[text()='CREATE']"));
        resultList.get(0).findElement(By.xpath("//td[text()='clients/" + id + "']"));

        adminEventsPage.table().reset();
        adminEventsPage.table().filterForm().addOperationType("UPDATE");
        adminEventsPage.table().update();

        resultList = adminEventsPage.table().rows();
        assertEquals(1, resultList.size());
        resultList.get(0).findElement(By.xpath("//td[text()='UPDATE']"));
        resultList.get(0).findElement(By.xpath("//td[text()='clients/" + id + "']"));

        adminEventsPage.table().reset();
        adminEventsPage.table().filterForm().addOperationType("DELETE");
        adminEventsPage.table().update();

        resultList = adminEventsPage.table().rows();
        assertEquals(1, resultList.size());
        resultList.get(0).findElement(By.xpath("//td[text()='DELETE']"));
        resultList.get(0).findElement(By.xpath("//td[text()='clients/" + id + "']"));
    }
}
