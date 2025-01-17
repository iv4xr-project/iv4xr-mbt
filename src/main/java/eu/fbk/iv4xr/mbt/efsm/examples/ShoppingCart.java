package eu.fbk.iv4xr.mbt.efsm.examples;

import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.EFSMBuilder;
import eu.fbk.iv4xr.mbt.efsm.EFSMContext;
import eu.fbk.iv4xr.mbt.efsm.EFSMGuard;
import eu.fbk.iv4xr.mbt.efsm.EFSMOperation;
import eu.fbk.iv4xr.mbt.efsm.EFSMProvider;
import eu.fbk.iv4xr.mbt.efsm.EFSMState;
import eu.fbk.iv4xr.mbt.efsm.EFSMTransition;
import eu.fbk.iv4xr.mbt.efsm.exp.Assign;
import eu.fbk.iv4xr.mbt.efsm.exp.Const;
import eu.fbk.iv4xr.mbt.efsm.exp.Var;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolNot;
import eu.fbk.iv4xr.mbt.efsm.exp.bool.BoolOr;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntEq;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntGreat;
import eu.fbk.iv4xr.mbt.efsm.exp.integer.IntSum;

public class ShoppingCart implements EFSMProvider{

	//Constant
	Const<Integer> three = new Const<Integer>(3);
	
	// States
    public EFSMState amazon = new EFSMState("v_Amazon");
    public EFSMState searchResult = new EFSMState("v_SearchResult");
    public EFSMState bookInformation = new EFSMState("v_BookInformation");
    public EFSMState addedToCart = new EFSMState("v_AddedToCart");
    public EFSMState shoppingCart = new EFSMState("v_ShoppingCart");
		
    // Context
    private Var<Integer> numOfBooks = new Var<Integer>("numOfBooks", 0);
    private EFSMContext context = new EFSMContext(numOfBooks);
	
    //Operations
	Assign<Integer> incCount = //Increment counter
			new Assign(numOfBooks, new IntSum(numOfBooks, new Const(1)));
    
	//Checks
	IntGreat countGreaterThanThree = // Is greater than 3
			new IntGreat(numOfBooks, three);
	
	IntEq countEqualThree = // Is equal to 3
			new IntEq(numOfBooks, three);

	BoolOr countGreaterOrEqualToThree = // Is greater than or equal to 3
			new BoolOr(countGreaterThanThree, countEqualThree);

	BoolNot countLessThanOrEqualToThree = // Less than or equal to 3
			new BoolNot(countGreaterOrEqualToThree);
	
	public ShoppingCart() {
		
	}
	
	@Override
	public EFSM getModel() {
		
		//Transitions
		
		//t_0 : vAmazon -> vAmazon # e_EnterBaseURL
		EFSMTransition t_0 = new EFSMTransition();
		
		//t_1 : vAmazon -> v_SearchResult # e_SearchBook
		EFSMTransition t_1 = new EFSMTransition();
		
		//t_2 : v_SearchResult -> v_ShoppingCart # e_ShoppingCart
		EFSMTransition t_2 = new EFSMTransition();
		
		//t_3 : v_SearchResult -> v_BookInformation # e_ClickBook
		EFSMTransition t_3 = new EFSMTransition();
		
		//t_4 : v_BookInformation -> v_SearchResult # e_SearchBook
		EFSMTransition t_4 = new EFSMTransition();
		
		//t_5 : v_BookInformation -> v_ShoppingCart # e_ShoppingCart
		EFSMTransition t_5 = new EFSMTransition();

		//t_6 : v_BookInformation -> v_AddedToCart # e_AddBookToCart
		EFSMTransition t_6 = new EFSMTransition();
		t_6.setGuard(new EFSMGuard(countLessThanOrEqualToThree));
		t_6.setOp(new EFSMOperation(incCount));
		
		//t_7 : v_AddedToCart -> v_SearchResult # e_SearchBook
		EFSMTransition t_7 = new EFSMTransition();

		//t_8 : v_AddedToCart -> v_ShoppingCart # e_ShoppingCart
		EFSMTransition t_8 = new EFSMTransition();
		
		//t_9 : v_ShoppingCart -> v_SearchResult # e_SearchResult
		EFSMTransition t_9 = new EFSMTransition();
		
		// Model and Associated builder
		EFSM shoppingCartEFSM;
		EFSMBuilder shoppingCartEFSMBuilder = new EFSMBuilder(EFSM.class);

		// Parameter generator 
		ShoppingCartParameterGenerator parameterGenerator = new ShoppingCartParameterGenerator();
		
		shoppingCartEFSM = shoppingCartEFSMBuilder
	    		.withTransition(amazon, amazon, t_0)
	    		.withTransition(amazon, searchResult, t_1)
	    		.withTransition(searchResult, shoppingCart, t_2)
	    		.withTransition(searchResult, bookInformation, t_3)
	    		.withTransition(bookInformation, searchResult, t_4)
	    		.withTransition(bookInformation, shoppingCart, t_5)
	    		.withTransition(bookInformation, addedToCart, t_6)
	    		.withTransition(addedToCart, searchResult, t_7)
	    		.withTransition(addedToCart, shoppingCart, t_8)
	    		.withTransition(shoppingCart, searchResult, t_9)
	    		.build(amazon,context, parameterGenerator);
	    
	    return(shoppingCartEFSM);
		
	}

}
