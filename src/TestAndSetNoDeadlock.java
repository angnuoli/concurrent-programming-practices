import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The method Test_and_set without deadlock.
 */
public class TestAndSetNoDeadlock {
    /**
     * The Lock is atomic variable.
     */
    private AtomicBoolean lock = new AtomicBoolean(false);
    private int a = 0;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws InterruptedException {
        TestAndSetNoDeadlock testAndSetNoDeadlock = new TestAndSetNoDeadlock();
        testAndSetNoDeadlock.run();
    }

    /**
     * Run.
     *
     * @throws InterruptedException the interrupted exception
     */
    private void run() throws InterruptedException {
        BusyInstance[] busyInstances = new BusyInstance[10];
        for (int i = 0; i < 10; i++) {
            busyInstances[i] = new BusyInstance(i);
            busyInstances[i].start();
        }
        for (int i = 0; i < 10; i++) {
            busyInstances[i].join();
        }
        System.out.println("Finally, a is " + a);
    }

    private class BusyInstance extends Thread implements Runnable {
        /**
         * The Id.
         */
        int id;

        /**
         * Instantiates a new Busy instance.
         *
         * @param id the id
         */
        BusyInstance(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                // replace self-implemented test_and_set, no deadlock
                while (lock.getAndSet(true)) ;
                a++;
                System.out.println("Instance" + id + ": a = " + a);
                lock.set(false);
            }
        }
    }
}


