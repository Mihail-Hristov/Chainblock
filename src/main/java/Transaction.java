public interface Transaction {
    public int getId();

    public TransactionStatus getStatus();

    public double getAmount();

    public void changeStatus(TransactionStatus status);
}
