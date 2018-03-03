/**
 * The Busy waiting method has race condition which is dangerous.
 */
public class BusyWaiting {
    private volatile int lock = 0;
    private int a = 0;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws InterruptedException {
        BusyWaiting busyWaiting = new BusyWaiting();
        busyWaiting.run();
    }

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
                // use loop to lock the resource, but still has race condition.
                while (lock == 1) ;
                lock = 1;
                a = a + 1;
                System.out.println("Instance" + id + ": a = " + a);
                lock = 0;
            }
        }
    }
}


