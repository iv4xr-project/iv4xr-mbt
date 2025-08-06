package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import eu.fbk.iv4xr.mbt.efsm.examples.ShoppingCart;

public class ShoppingCartTest {

	@Test
    public void testInitialState() {
        ShoppingCart sc = new ShoppingCart();
        EFSM m = sc.getModel();
        
        assertTrue(m.curState.equals(sc.amazon));
        assertTrue((Integer)m.curContext.getContext().getVariable("numOfBooks").getValue() == 0);
    }

    @Test
    public void testSearchAndAddBooks() {
        ShoppingCart sc = new ShoppingCart();
        EFSM m = sc.getModel();
        
        // Initially at Amazon base state
        assertTrue(m.curState.equals(sc.amazon));
        
        // Transition to SearchResult via SearchBook event
        m.transition(null, sc.searchResult);
        assertTrue(m.curState.equals(sc.searchResult));
        
        //Book information
        m.transition(null, sc.bookInformation);
        assertTrue(m.curState.equals(sc.bookInformation));
        
        // Add book to cart, assume guard condition of numOfBooks <= 3 is met
        m.transition(null, sc.addedToCart);
        assertTrue(m.curState.equals(sc.addedToCart));
        assertTrue((Integer)m.curContext.getContext().getVariable("numOfBooks").getValue() == 1);
        
        // Return to search results to add another book
        m.transition(null, sc.searchResult);
        assertTrue(m.curState.equals(sc.searchResult));
        
        // Try to add two more books
        for (int i = 0; i < 2; i++) {
            m.transition(null, sc.bookInformation);
            assertTrue(m.curState.equals(sc.bookInformation));      
            m.transition(null, sc.addedToCart);   
            assertTrue(m.curState.equals(sc.addedToCart));
            m.transition(null, sc.searchResult);
            assertTrue(m.curState.equals(sc.searchResult));
        }
        
        // Check if the number of books is now 3
        assertTrue((Integer)m.curContext.getContext().getVariable("numOfBooks").getValue() == 3);
        
        // Attempt to add another book, should fail because numOfBooks > 3
        m.transition(null, sc.bookInformation);
        assertTrue(m.curState.equals(sc.bookInformation));
        m.transition(null, sc.addedToCart);
        assertFalse(m.curState.equals(sc.addedToCart)); // No transition should happen
    }

    @Test
    public void testCheckoutProcess() {
        ShoppingCart sc = new ShoppingCart();
        EFSM m = sc.getModel();
        
        // Navigate to shopping cart from search results
        m.transition(null, sc.searchResult);
        assertTrue(m.curState.equals(sc.searchResult));
        m.transition(null, sc.shoppingCart);
        assertTrue(m.curState.equals(sc.shoppingCart));
        
        // Check out and return to search results
        m.transition(null, sc.searchResult);
        assertTrue(m.curState.equals(sc.searchResult));
    }
	
}
