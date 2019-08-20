import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class TestingPorts {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<boolean[]>> taskList = new ArrayList<>();
        for(int i = 0;i <=80;i= i+10){
            taskList.add(new PortCheckingThread("www.google.com",i,i+9));
        }
        List<Future<boolean[]>> futures = executorService.invokeAll(taskList);

        for(Future<boolean[]> future: futures) {
            // The result is printed only after all the futures are complete. (i.e. after 5 seconds)
            System.out.println(Arrays.toString(future.get()));
        }

        executorService.shutdown();
    }
}
