package eg.edu.alexu.csd.oop.test.jdbc;
import org.junit.Assert;
import org.junit.Test;

import eg.edu.alexu.csd.oop.db.Database;

public class IntegrationTest {

    public static Class<?> getSpecifications(){
        return Database.class;
    }
    
    @Test
    public void test() {
        Assert.assertNotNull("Failed to create DBMS implemenation",  (Database)eg.edu.alexu.csd.oop.TestRunner.getImplementationInstanceForInterface(Database.class));
    }

}
