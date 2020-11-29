import org.apache.zookeeper.KeeperException;

public class Cliente {

    ZookeeperHelper.Queue queue;
    ZookeeperHelper.Lock lock;
    ZookeeperHelper.Leader leader;

    private void colocarElementoNaFila(int i) throws KeeperException, InterruptedException {
        queue.produce(i);
    }

    private void consomeElementoDaFila(int i) throws KeeperException, InterruptedException {
        queue.consume();
    }

}
