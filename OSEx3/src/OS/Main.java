package OS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*
Tamir Abutbul, 208206771
Yuval Katzir , 318644861
Ofir  Mutzafi, 208298653
*/

public class Main {

    private static class ProcessComp implements Comparable {
        private int arrivalTime;
        private int computationTime;

        @Override
        public String toString() {
            return arrivalTime + "," + computationTime;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(int arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public int getComputationTime() {
            return computationTime;
        }

        public void setComputationTime(int computationTime) {
            this.computationTime = computationTime;
        }

        ProcessComp(int arrival, int computation) {
            this.arrivalTime = arrival;
            this.computationTime = computation;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProcessComp that = (ProcessComp) o;
            return arrivalTime == that.arrivalTime;
        }

        @Override
        public int hashCode() {
            return Objects.hash(arrivalTime);
        }

        @Override
        public int compareTo(Object o) {
            ProcessComp that = (ProcessComp) o;
            if (this.arrivalTime > that.arrivalTime) {
                return 1;
            } else if (this.arrivalTime < that.arrivalTime) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private static class ProcessJobComp extends ProcessComp {

        ProcessJobComp(int arrival, int computation) {
            super(arrival, computation);
        }

        ProcessJobComp(ProcessJobComp pjc) {
            super(pjc.getArrivalTime(), pjc.getComputationTime());
        }

        ProcessJobComp(ProcessComp pc) {
            super(pc.getArrivalTime(), pc.getComputationTime());
        }

        @Override
        public int compareTo(Object o) {
            ProcessJobComp that = (ProcessJobComp) o;
            if (this.getComputationTime() > that.getComputationTime()) {
                return 1;
            } else if (this.getComputationTime() < that.getComputationTime()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        Queue<ProcessComp> priorityQueue = new PriorityQueue<>();
        String fileName="";
        String line;
        String[] splitInput;
        BufferedReader in = null;
        final int NumofFiles = 5;
        int nNumberOfProc;

        for (int nFileIndex = 0;nFileIndex < NumofFiles;nFileIndex++) {
            fileName = args[nFileIndex];
            try {
                in = new BufferedReader(new FileReader(fileName));
                nNumberOfProc = Integer.parseInt(in.readLine());
                System.out.println(fileName + ":\n");
                while ((line = in.readLine()) != null) {
                    splitInput = line.split(",");
                    priorityQueue.add(new ProcessComp(Integer.parseInt(splitInput[0]), Integer.parseInt(splitInput[1])));
                }
                main.FCFS(priorityQueue, nNumberOfProc);
                main.LCFSNP(priorityQueue, nNumberOfProc);
                main.LCFSP(priorityQueue, nNumberOfProc);
                main.RR(priorityQueue, nNumberOfProc, 2);
                main.SJF(priorityQueue, nNumberOfProc);
                priorityQueue.removeAll(priorityQueue);
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void FCFS(Queue<ProcessComp> pQueue, int nNumberOfProc) {
        int nTotalTime = 0;
        int nCurrentTime = 0;

        Queue<ProcessComp> processCompQueue = new PriorityQueue<>();
        processCompQueue.addAll(pQueue);
        ProcessComp pc;

        while (!processCompQueue.isEmpty()) {
            pc = processCompQueue.poll();
            if (nCurrentTime < pc.getArrivalTime())
                nCurrentTime = pc.getArrivalTime();
            nCurrentTime += pc.getComputationTime();
            nTotalTime += nCurrentTime - pc.getArrivalTime();
        }

        System.out.println("FCFS: mean turnaround = " + (float) nTotalTime / (float) nNumberOfProc + "\n");
    }

    public void LCFSNP(Queue<ProcessComp> pQueue, int nNumberOfProc) {
        int nTotalTime = 0;
        int nCurrentTime = 0;
        boolean isProcessRunning = false;

        Queue<ProcessComp> processCompQueue = new PriorityQueue<>();
        processCompQueue.addAll(pQueue);
        Stack<ProcessComp> stack = new Stack<>();
        ProcessComp pc;

        while (!processCompQueue.isEmpty() || !stack.isEmpty()) {
            if (!isProcessRunning) {
                if (stack.isEmpty()) {
                    pc = processCompQueue.poll();
                    stack.add(pc);

                    // While other process come at the same time add them as well to the stack as last arriving
                    while (!processCompQueue.isEmpty() && pc.arrivalTime == processCompQueue.peek().arrivalTime) {
                        pc = processCompQueue.poll();
                        stack.add(pc);
                    }

                    isProcessRunning = true;
                } else { // Stack not empty
                    isProcessRunning = true;
                }
            } else { // a process is currently running
                pc = stack.pop();
                if (nCurrentTime < pc.getArrivalTime())
                    nCurrentTime = pc.getArrivalTime();
                nCurrentTime += pc.getComputationTime();
                nTotalTime += nCurrentTime - pc.getArrivalTime();

                // Adds process that arrived while we handled a process
                while (!processCompQueue.isEmpty() && processCompQueue.peek().arrivalTime <= nCurrentTime) {
                    pc = processCompQueue.poll();
                    stack.add(pc);
                }

                isProcessRunning = false;
            }
        }

        System.out.println("LCFS (NP): mean turnaround = " + (float) nTotalTime / (float) nNumberOfProc + "\n");
    }

    public void LCFSP(Queue<ProcessComp> pQueue, int nNumberOfProc) {
        int nTotalTime = 0;
        int nCurrentTime = 0;
        boolean isProcessRunning = false;

        Queue<ProcessComp> processCompQueue = new PriorityQueue<>();
        processCompQueue.addAll(pQueue);
        Stack<ProcessComp> stack = new Stack<>();
        ProcessComp pc;

        while (!processCompQueue.isEmpty() || !stack.isEmpty()) {
            if (!isProcessRunning) {
                if (stack.isEmpty()) {
                    pc = processCompQueue.poll();
                    stack.add(pc);

                    // While other process come at the same time add them as well to the stack as last arriving
                    while (!processCompQueue.isEmpty() && pc.arrivalTime == processCompQueue.peek().arrivalTime) {
                        pc = processCompQueue.poll();
                        stack.add(pc);
                    }

                    isProcessRunning = true;
                } else { // Stack not empty
                    isProcessRunning = true;
                }
            } else { // a process is currently running
                pc = stack.pop();
                int nTimeStemps;
                boolean isNewProcess = false;

                if (nCurrentTime < pc.getArrivalTime())
                    nCurrentTime = pc.getArrivalTime();
                nTimeStemps = nCurrentTime;

                while (nTimeStemps < nCurrentTime + pc.getComputationTime() && !isNewProcess) {
                    nTimeStemps++;
                    if (!processCompQueue.isEmpty() && nTimeStemps >= processCompQueue.peek().getArrivalTime()) {
                        isNewProcess = true;
                        if (nTimeStemps < nCurrentTime + pc.getComputationTime()) {
                            stack.add(new ProcessComp(pc.getArrivalTime(),
                                    nCurrentTime + pc.getComputationTime() - nTimeStemps));
                        }
                    }
                }

                // Reached the end of its computation time
                if (nTimeStemps >= nCurrentTime + pc.getComputationTime())
                    nTotalTime += nTimeStemps - pc.getArrivalTime();
                nCurrentTime = nTimeStemps;

                // Adds process that arrived while we handled a process
                while (!processCompQueue.isEmpty() && processCompQueue.peek().arrivalTime <= nCurrentTime) {
                    pc = processCompQueue.poll();
                    stack.add(pc);
                }

                isProcessRunning = false;
            }
        }

        System.out.println("LCFS (P): mean turnaround = " + (float) nTotalTime / (float) nNumberOfProc + "\n");
    }

    public void RR(Queue<ProcessComp> pQueue, int nNumberOfProc, int nQuantumTime) {
        int nTotalTime = 0;
        int nCurrentTime = 0;
        boolean isProcessRunning = false;

        Queue<ProcessComp> processCompQueue = new PriorityQueue<>();
        processCompQueue.addAll(pQueue);
        Queue<ProcessComp> queue = new LinkedList<>();
        ProcessComp pc;

        while (!processCompQueue.isEmpty() || !queue.isEmpty()) {
            if (!isProcessRunning) {
                if (queue.isEmpty()) {
                    pc = processCompQueue.poll();
                    queue.add(pc);

                    // While other process come at the same time add them as well to the stack as last arriving
                    while (!processCompQueue.isEmpty() && pc.arrivalTime == processCompQueue.peek().arrivalTime) {
                        pc = processCompQueue.poll();
                        queue.add(pc);
                    }

                    isProcessRunning = true;
                } else { // Stack not empty
                    isProcessRunning = true;
                }
            } else { // a process is currently running
                pc = queue.poll();
                int nTimeStemps;
                boolean isNewProcess = false;

                if (nCurrentTime < pc.getArrivalTime())
                    nCurrentTime = pc.getArrivalTime();
                nTimeStemps = nCurrentTime;

                while (nTimeStemps < nCurrentTime + nQuantumTime
                        && nTimeStemps < nCurrentTime + pc.getComputationTime()) {
                    nTimeStemps++;

                    // checks if something came while we are processing
                    if (!processCompQueue.isEmpty() && nTimeStemps >= processCompQueue.peek().getArrivalTime()) {
                        queue.add(processCompQueue.poll());
                    }

                    if (nTimeStemps == nCurrentTime + nQuantumTime
                            && nTimeStemps != nCurrentTime + pc.getComputationTime()) {
                        queue.add(new ProcessComp(pc.getArrivalTime(),
                                nCurrentTime + pc.getComputationTime() - nTimeStemps));
                    }
                }

                // Reached the end of its computation time
                if (nTimeStemps >= nCurrentTime + pc.getComputationTime())
                    nTotalTime += nTimeStemps - pc.getArrivalTime();
                nCurrentTime = nTimeStemps;

                // Adds process that arrived while we handled a process
                while (!processCompQueue.isEmpty() && processCompQueue.peek().arrivalTime <= nCurrentTime) {
                    pc = processCompQueue.poll();
                    queue.add(pc);
                }

                isProcessRunning = false;
            }
        }

        System.out.println("RR: mean turnaround = " + (float) nTotalTime / (float) nNumberOfProc + "\n");
    }

    public void SJF(Queue<ProcessComp> pQueue, int nNumberOfProc) { // Preemptive
        int nTotalTime = 0;
        int nCurrentTime = 0;
        boolean isProcessRunning = false;

        Queue<ProcessComp> processCompQueue = new PriorityQueue<>();
        processCompQueue.addAll(pQueue);
        Queue<ProcessComp> queue = new PriorityQueue<>();
        ProcessComp pc;

        while (!processCompQueue.isEmpty() || !queue.isEmpty()) {
            if (!isProcessRunning) {
                if (queue.isEmpty()) {
                    pc = processCompQueue.poll();
                    queue.add(new ProcessJobComp(pc));

                    // While other process come at the same time add them as well to the stack as last arriving
                    while (!processCompQueue.isEmpty() && pc.arrivalTime == processCompQueue.peek().arrivalTime) {
                        pc = processCompQueue.poll();
                        queue.add(new ProcessJobComp(pc));
                    }

                    isProcessRunning = true;
                } else { // Stack not empty
                    isProcessRunning = true;
                }
            } else { // a process is currently running
                pc = queue.poll();
                int nTimeStemps;

                if (nCurrentTime < pc.getArrivalTime())
                    nCurrentTime = pc.getArrivalTime();
                nTimeStemps = nCurrentTime;

                while (nTimeStemps < nCurrentTime + pc.getComputationTime()) {
                    nTimeStemps++;

                    // checks if something came while we are processing
                    if (!processCompQueue.isEmpty() && nTimeStemps >= processCompQueue.peek().getArrivalTime()) {
                        if (nTimeStemps < nCurrentTime + pc.getComputationTime()) {
                            queue.add(new ProcessJobComp(pc.getArrivalTime(),
                                    nCurrentTime + pc.getComputationTime() - nTimeStemps));
                        }
                        queue.add(new ProcessJobComp(processCompQueue.poll()));
                        break;
                    }
                }

                // Reached the end of its computation time
                if (nTimeStemps >= nCurrentTime + pc.getComputationTime())
                    nTotalTime += nTimeStemps - pc.getArrivalTime();
                nCurrentTime = nTimeStemps;

                // Adds process that arrived while we handled a process
                while (!processCompQueue.isEmpty() && processCompQueue.peek().arrivalTime <= nCurrentTime) {
                    pc = processCompQueue.poll();
                    queue.add(new ProcessJobComp(pc));
                }

                isProcessRunning = false;
            }
        }

        System.out.println("SJF: mean turnaround = " + (float) nTotalTime / (float) nNumberOfProc + "\n");
    }
}