package eg.edu.alexu.csd.oop.test.db;
import java.sql.Driver;

import eg.edu.alexu.csd.oop.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class IntegrationTest {

    public static Class<?> getSpecifications(){
        return Driver.class;
    }
    
    @Test
    public void test() {
        Assert.assertNotNull("Failed to create Driver implemenation",  (Driver) TestRunner.getImplementationInstanceForInterface(Driver.class));
    }

}
