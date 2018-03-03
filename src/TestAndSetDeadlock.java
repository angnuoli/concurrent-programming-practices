/**
 * This method uses Test_and_set to synchronize processes, however
 * it will result in deadlock.
 */
public class TestAndSetDeadlock {
    /**
     * The lock can be seen by each thread.
     */
    private volatile int lock = 0;
    private int a = 0;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws InterruptedException {
        TestAndSetDeadlock testAndSetDeadlock = new TestAndSetDeadlock();
        testAndSetDeadlock.run();
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
                while (testAndSet() == 1) ;
                a++;
                System.out.println("Instance" + id + ": a = " + a);
                lock = 0;
            }
        }


    }

    /**
     * Test_and_set method. Expect to be atomic.
     */
    synchronized private int testAndSet() {
        int temp = lock;
        lock = 1;
        return temp;
    }
}


