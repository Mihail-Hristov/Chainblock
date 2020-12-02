import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChainblockImpl implements Chainblock{
    private Map<Integer, Transaction> chainblock;

    public ChainblockImpl() {
        this.chainblock = new HashMap<>();
    }

    public int getCount() {
        return this.chainblock.size();
    }

    public void add(Transaction transaction) {
        this.chainblock.putIfAbsent(transaction.getId(), transaction);
    }

    public boolean contains(Transaction transaction) {
        if (this.contains(transaction.getId())) {
            return true;
        }

        return false;
    }

    public boolean contains(int id) {
        if (chainblock.containsKey(id)) {
            return true;
        }

        return false;
    }

    public void changeTransactionStatus(int id, TransactionStatus newStatus) {
        if (!chainblock.containsKey(id)) {
            throw new IllegalArgumentException("No such ID");
        }

        chainblock.get(id).changeStatus(newStatus);
    }

    public void removeTransactionById(int id) {
        if (!chainblock.containsKey(id)) {
            throw new IllegalArgumentException("No such ID");
        }

        chainblock.remove(id);
    }

    public Transaction getById(int id) {
        if (!chainblock.containsKey(id)) {
            throw new IllegalArgumentException("No such ID");
        }

        return chainblock.get(id);
    }

    public Iterable<Transaction> getByTransactionStatus(TransactionStatus status) {
        
    }

    public Iterable<String> getAllSendersWithTransactionStatus(TransactionStatus status) {
        return null;
    }

    public Iterable<String> getAllReceiversWithTransactionStatus(TransactionStatus status) {
        return null;
    }

    public Iterable<Transaction> getAllOrderedByAmountDescendingThenById() {
        return null;
    }

    public Iterable<Transaction> getBySenderOrderedByAmountDescending(String sender) {
        return null;
    }

    public Iterable<Transaction> getByReceiverOrderedByAmountThenById(String receiver) {
        return null;
    }

    public Iterable<Transaction> getByTransactionStatusAndMaximumAmount(TransactionStatus status, double amount) {
        return null;
    }

    public Iterable<Transaction> getBySenderAndMinimumAmountDescending(String sender, double amount) {
        return null;
    }

    public Iterable<Transaction> getByReceiverAndAmountRange(String receiver, double lo, double hi) {
        return null;
    }

    public Iterable<Transaction> getAllInAmountRange(double lo, double hi) {
        return null;
    }

    public Iterator<Transaction> iterator() {
        return null;
    }
}
