import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChainblockTest {
    private Chainblock chainblock;

    @Before
    public void setUp() {
        chainblock = new ChainblockImpl();
    }

    @Test
    public void testGetCountReturnCorrectCountOfTransactions() {
        Transaction transaction = createOneTransaction();
        Assert.assertEquals(0, chainblock.getCount());
        chainblock.add(transaction);
        Assert.assertEquals(1, chainblock.getCount());
    }

    @Test
    public void testAddCorrectlyAddedCurrentTransactionToTheDataStore() {
        int count = 10;
        chainblock = fillTheChainblock(count);
        Transaction transaction = createOneTransaction();
        chainblock.add(transaction);
        Assert.assertEquals(count + 1, chainblock.getCount());
        Assert.assertTrue(chainblock.contains(transaction));

    }

    @Test
    public void testContainsReturnCorrectBooleanStatusByTransaction() {
        Transaction transaction = createOneTransaction();
        Assert.assertFalse(chainblock.contains(transaction));
        chainblock.add(transaction);
        Assert.assertTrue(chainblock.contains(transaction));
    }

    @Test
    public void testContainsReturnCorrectBooleanStatusById() {
        int id = 1948;
        Transaction transaction = new TransactionImpl(id, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        Assert.assertFalse(chainblock.contains(id));
        chainblock.add(transaction);
        Assert.assertTrue(chainblock.contains(id));
    }

    @Test
    public void testChangeTransactionStatusCorrectChangeTheStatusParameter() {
        int id = 1948;
        Transaction transaction = new TransactionImpl(id, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        chainblock.add(transaction);
        chainblock.changeTransactionStatus(id, TransactionStatus.SUCCESSFUL);
        Assert.assertEquals(TransactionStatus.SUCCESSFUL, chainblock.getById(id).getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeTransactionStatusWithNoPresentTransactionId() {
        int id = 1948;
        Transaction transaction = new TransactionImpl(id, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        chainblock.add(transaction);
        chainblock.changeTransactionStatus(id + 5, TransactionStatus.SUCCESSFUL);
    }

    @Test
    public void testRemoveTransactionByIdRemoveCorrectlyCurrentStatus() {
        int id = 1948;
        int id2 = 1997;
        Transaction transaction = new TransactionImpl(id, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        Transaction transaction2 = new TransactionImpl(id2, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        chainblock.add(transaction);
        chainblock.add(transaction2);
        Assert.assertTrue(chainblock.contains(id));
        chainblock.removeTransactionById(id);
        Assert.assertFalse(chainblock.contains(id));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTransactionByIdWhenNoSuchIdInTheChainblock() {
        int id = 1948;
        int id2 = 1997;
        Transaction transaction = new TransactionImpl(id, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        chainblock.add(transaction);
        chainblock.removeTransactionById(id2);
    }

    @Test
    public void testGetByIdReturnCorrectTransaction() {
        int id = 1948;
        int id2 = 1997;
        Transaction transaction = new TransactionImpl(id, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        Transaction transaction2 = new TransactionImpl(id2, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        chainblock.add(transaction);
        chainblock.add(transaction2);
        Transaction returnedTransaction = chainblock.getById(id2);
        Assert.assertNotNull(returnedTransaction);
        Assert.assertEquals(transaction2.getId(), returnedTransaction.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdReturnWhenNpSuchId() {
        int id = 1948;
        int id2 = 1997;
        Transaction transaction = new TransactionImpl(id, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        Transaction transaction2 = new TransactionImpl(id2, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        chainblock.add(transaction);
        chainblock.add(transaction2);
        Transaction returnedTransaction = chainblock.getById(id2 + 10);
    }

    @Test
    public void testGetByTransactionStatusReturnCorrectTransactions() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.ABORTED, "From_Test", "To_Test", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test", "To_Test", 500);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);

        Iterable<Transaction> transactions = chainblock.getByTransactionStatus(TransactionStatus.SUCCESSFUL);
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(2, returnedTransactions.size());

        int id = 3;
        for (Transaction t : returnedTransactions) {
            Assert.assertEquals(id--, t.getId());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByTransactionStatusWhenNoSuchStatusPresent() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.ABORTED, "From_Test", "To_Test", 400);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);

        Iterable<Transaction> transactions = chainblock.getByTransactionStatus(TransactionStatus.FAILED);
    }

    @Test
    public void testGetAllSendersWithTransactionStatusReturnCorrectTransaction() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test4", "To_Test", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test5", "To_Test", 500);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);

        Iterable<String> transactions = chainblock.getAllSendersWithTransactionStatus(TransactionStatus.SUCCESSFUL);
        Assert.assertNotNull(transactions);
        List<String> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(3, returnedTransactions.size());

        Assert.assertEquals("From_Test2", returnedTransactions.get(0));
        Assert.assertEquals("From_Test2", returnedTransactions.get(1));
        Assert.assertEquals("From_Test4", returnedTransactions.get(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllSendersWithTransactionStatusWhenNoSuchStatusPresent() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test4", "To_Test", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test5", "To_Test", 500);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);

        Iterable<String> transactions = chainblock.getAllSendersWithTransactionStatus(TransactionStatus.ABORTED);
    }

    @Test
    public void testGetAllReceiverWithTransactionStatusReturnCorrectTransaction() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test", "To_Test5", 500);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);

        Iterable<String> transactions = chainblock.getAllReceiversWithTransactionStatus(TransactionStatus.SUCCESSFUL);
        Assert.assertNotNull(transactions);
        List<String> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(3, returnedTransactions.size());

        Assert.assertEquals("To_Test2", returnedTransactions.get(0));
        Assert.assertEquals("To_Test2", returnedTransactions.get(1));
        Assert.assertEquals("To_Test2", returnedTransactions.get(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllReceiverWithTransactionStatusWhenNoSuchStatusPresent() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test", "To_Test5", 500);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);

        Iterable<String> transactions = chainblock.getAllReceiversWithTransactionStatus(TransactionStatus.ABORTED);
    }

    @Test
    public void testGetAllOrderedByAmountDescendingThenByIdReturnAllItemInCorrectOrder() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 200);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test", "To_Test5", 400);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Test", "To_Test5", 600);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);

        Iterable<Transaction> transactions = chainblock.getAllOrderedByAmountDescendingThenById();
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(7, returnedTransactions.size());

        Assert.assertEquals(700, returnedTransactions.get(0).getAmount(), 0);
        Assert.assertEquals(7, returnedTransactions.get(0).getId());
        Assert.assertEquals(600, returnedTransactions.get(1).getAmount(), 0);
        Assert.assertEquals(6, returnedTransactions.get(1).getId());
        Assert.assertEquals(400, returnedTransactions.get(2).getAmount(), 0);
        Assert.assertEquals(4, returnedTransactions.get(2).getId());
        Assert.assertEquals(400, returnedTransactions.get(3).getAmount(), 0);
        Assert.assertEquals(5, returnedTransactions.get(3).getId());
        Assert.assertEquals(200, returnedTransactions.get(4).getAmount(), 0);
        Assert.assertEquals(2, returnedTransactions.get(4).getId());
        Assert.assertEquals(200, returnedTransactions.get(5).getAmount(), 0);
        Assert.assertEquals(3, returnedTransactions.get(5).getId());
        Assert.assertEquals(100, returnedTransactions.get(6).getAmount(), 0);
        Assert.assertEquals(1, returnedTransactions.get(6).getId());
    }

    @Test
    public void testGetBySenderOrderByAmountDescendingReturnCorrectTransaction() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test4", "To_Test", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test5", "To_Test", 500);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);

        Iterable<Transaction> transactions = chainblock.getBySenderOrderedByAmountDescending("From_Test2");
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(2, returnedTransactions.size());

        Assert.assertEquals(3, returnedTransactions.get(0).getId());
        Assert.assertEquals(2, returnedTransactions.get(1).getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBySenderOrderByAmountDescendingWhenNowSuchSender() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test4", "To_Test", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test5", "To_Test", 500);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);

        Iterable<Transaction> transactions = chainblock.getBySenderOrderedByAmountDescending("From_Test2");
    }

    @Test
    public void testGetByReceiverOrderedByAmountThanByIdReturnCorrectTransaction() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 200);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 200);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test", "To_Tes4", 500);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Test", "To_Test5", 600);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);

        Iterable<Transaction> transactions = chainblock.getByReceiverOrderedByAmountThenById("T0_Test4");
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(3, returnedTransactions.size());

        Assert.assertEquals(500, returnedTransactions.get(0).getAmount(), 0);
        Assert.assertEquals(5, returnedTransactions.get(0).getId());
        Assert.assertEquals(200, returnedTransactions.get(1).getAmount(), 0);
        Assert.assertEquals(3, returnedTransactions.get(1).getId());
        Assert.assertEquals(200, returnedTransactions.get(2).getAmount(), 0);
        Assert.assertEquals(4, returnedTransactions.get(2).getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByReceiverOrderedByAmountThanByIdWhenNoSuchReceiverExist() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 200);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 200);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test", "To_Tes4", 500);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Test", "To_Test5", 600);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);

        Iterable<Transaction> transactions = chainblock.getByReceiverOrderedByAmountThenById("T0_Test10");
    }


    @Test
    public void testGetByTransactionStatusAndMaximumAmountReturnCorrectTransaction() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test", "To_Tes4", 500);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Test", "To_Test5", 600);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);

        Iterable<Transaction> transactions = chainblock.getByTransactionStatusAndMaximumAmount(TransactionStatus.SUCCESSFUL, 300);
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(2, returnedTransactions.size());

        Assert.assertEquals(300, returnedTransactions.get(0).getAmount(), 0);
        Assert.assertEquals(3, returnedTransactions.get(0).getId());
        Assert.assertEquals(200, returnedTransactions.get(1).getAmount(), 0);
        Assert.assertEquals(2, returnedTransactions.get(1).getId());
    }

    @Test
    public void testGetByTransactionStatusAndMaximumAmountReturnEmptyCollectionWhenNoSuchTransactionStatusExist() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test", "To_Tes4", 500);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Test", "To_Test5", 600);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);

        Iterable<Transaction> transactions = chainblock.getByTransactionStatusAndMaximumAmount(TransactionStatus.ABORTED, 300);
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertTrue(returnedTransactions.isEmpty());
    }

    @Test
    public void testGetByTransactionStatusAndMaximumAmountReturnEmptyCollectionWhenNoSuchAmountWasFound() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Test", "To_Tes4", 500);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Test", "To_Test5", 600);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);

        Iterable<Transaction> transactions = chainblock.getByTransactionStatusAndMaximumAmount(TransactionStatus.SUCCESSFUL, 100);
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertTrue(returnedTransactions.isEmpty());
    }

    @Test
    public void testGetBySenderAndMinimumAmountDescendingReturnCorrectTransactions() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Tes3", "To_Tes4", 500);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Test6", "To_Test5", 600);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test7", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);

        Iterable<Transaction> transactions = chainblock.getBySenderAndMinimumAmountDescending("From_Test3", 400);
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(1, returnedTransactions.size());

        Assert.assertEquals(500, returnedTransactions.get(0).getAmount(), 0);
        Assert.assertEquals(5, returnedTransactions.get(0).getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBySenderAndMinimumAmountDescendingWhenNoSuchSender() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Tes3", "To_Tes4", 500);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Test6", "To_Test5", 600);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test7", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);

        Iterable<Transaction> transactions = chainblock.getBySenderAndMinimumAmountDescending("From_Test10", 400);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBySenderAndMinimumAmountDescendingWhenNoAmountMoreThanInputted() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.FAILED, "From_Tes3", "To_Tes4", 500);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Test6", "To_Test5", 600);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test7", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);

        Iterable<Transaction> transactions = chainblock.getBySenderAndMinimumAmountDescending("From_Test3", 900);
    }

    @Test
    public void testGetByReceiverAndAmountRangeReturnCorrectTransactions() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 400);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Tes3", "To_Tes4", 500);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test6", "To_Test4", 600);
        Transaction transaction8 = new TransactionImpl(8, TransactionStatus.FAILED, "From_Test7", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);
        chainblock.add(transaction8);

        Iterable<Transaction> transactions = chainblock.getByReceiverAndAmountRange("To_Test4", 400, 600);
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(3, returnedTransactions.size());

        Assert.assertEquals(500, returnedTransactions.get(0).getAmount(), 0);
        Assert.assertEquals(6, returnedTransactions.get(0).getId());
        Assert.assertEquals(400, returnedTransactions.get(1).getAmount(), 0);
        Assert.assertEquals(4, returnedTransactions.get(1).getId());
        Assert.assertEquals(400, returnedTransactions.get(2).getAmount(), 0);
        Assert.assertEquals(5, returnedTransactions.get(2).getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByReceiverAndAmountRangeWhenNoSuchReceiver() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 400);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Tes3", "To_Tes4", 500);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test6", "To_Test4", 600);
        Transaction transaction8 = new TransactionImpl(8, TransactionStatus.FAILED, "From_Test7", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);
        chainblock.add(transaction8);

        Iterable<Transaction> transactions = chainblock.getByReceiverAndAmountRange("To_Test10", 400, 600);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByReceiverAndAmountRangeWhenNoAmountInGivenRange() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 400);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 400);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Tes3", "To_Tes4", 500);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test6", "To_Test4", 600);
        Transaction transaction8 = new TransactionImpl(8, TransactionStatus.FAILED, "From_Test7", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);
        chainblock.add(transaction8);

        Iterable<Transaction> transactions = chainblock.getByReceiverAndAmountRange("To_Test3", 100, 300);
    }

    @Test
    public void testGetAllInAmountRangeReturnCorrectTransaction() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 450);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 350);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Tes3", "To_Tes4", 500);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test6", "To_Test4", 600);
        Transaction transaction8 = new TransactionImpl(8, TransactionStatus.FAILED, "From_Test7", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);
        chainblock.add(transaction8);

        Iterable<Transaction> transactions = chainblock.getAllInAmountRange(300, 500);
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertEquals(4, returnedTransactions.size());

        Assert.assertEquals(300, returnedTransactions.get(0).getAmount(), 0);
        Assert.assertEquals(3, returnedTransactions.get(0).getId());
        Assert.assertEquals(450, returnedTransactions.get(1).getAmount(), 0);
        Assert.assertEquals(4, returnedTransactions.get(1).getId());
        Assert.assertEquals(350, returnedTransactions.get(2).getAmount(), 0);
        Assert.assertEquals(5, returnedTransactions.get(2).getId());
        Assert.assertEquals(500, returnedTransactions.get(2).getAmount(), 0);
        Assert.assertEquals(6, returnedTransactions.get(2).getId());
    }

    @Test
    public void testGetAllInAmountRangeReturnEmptyCollectionWhenNoSuchTransaction() {
        Transaction transaction = new TransactionImpl(1, TransactionStatus.UNAUTHORIZED, "From_Test1", "To_Test1", 100);
        Transaction transaction2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From_Test2", "To_Test2", 200);
        Transaction transaction3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 300);
        Transaction transaction4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 450);
        Transaction transaction5 = new TransactionImpl(5, TransactionStatus.SUCCESSFUL, "From_Test3", "To_Test4", 350);
        Transaction transaction6 = new TransactionImpl(6, TransactionStatus.FAILED, "From_Tes3", "To_Tes4", 500);
        Transaction transaction7 = new TransactionImpl(7, TransactionStatus.FAILED, "From_Test6", "To_Test4", 600);
        Transaction transaction8 = new TransactionImpl(8, TransactionStatus.FAILED, "From_Test7", "To_Test5", 700);

        chainblock.add(transaction);
        chainblock.add(transaction2);
        chainblock.add(transaction3);
        chainblock.add(transaction4);
        chainblock.add(transaction5);
        chainblock.add(transaction6);
        chainblock.add(transaction7);
        chainblock.add(transaction8);

        Iterable<Transaction> transactions = chainblock.getAllInAmountRange(310, 340);
        Assert.assertNotNull(transactions);
        List<Transaction> returnedTransactions = createListFromIterable(transactions);
        Assert.assertTrue(returnedTransactions.isEmpty());
    }

    private <T> List<T> createListFromIterable(Iterable<T> transactions) {
        List<T> result = new ArrayList<>();

        for (T t : transactions) {
            result.add(t);
        }

        return result;
    }


    private static Transaction createOneTransaction() {
        return new TransactionImpl(1948, TransactionStatus.UNAUTHORIZED, "From_Test", "To_Test", 100);
    }

    private static List<TransactionImpl> createMultiplyTransactions(int count) {
        List<TransactionImpl> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            result.add(new TransactionImpl(1997 + i, TransactionStatus.UNAUTHORIZED, "From_Test" + i, "To_Test" + i, 100 + i));
        }

        return result;
    }

    private static Chainblock fillTheChainblock(int count) {
        Chainblock chainblock = new ChainblockImpl();
        List<TransactionImpl> transactions = createMultiplyTransactions(count);
        for (Transaction transaction : transactions) {
            chainblock.add(transaction);
        }

        return chainblock;
    }
}
