package com.example.javafx;

import java.io.InputStream;
import java.io.OutputStream;

//Class to handle the synchronization of input and output streams between processes.
class SyncPipe implements Runnable {
    //Reference to the output stream to which data will be written.
    private final OutputStream ostrm_;
    //Reference to the input stream for which data will be read.
    private final InputStream istrm_;

    //Constructor to initialize input and output streams.
    public SyncPipe(InputStream istrm, OutputStream ostrm) {
        istrm_ = istrm;
        ostrm_ = ostrm;
    }

    //Method to run the thread and handle the synchronization of streams.
    public void run() {
        try {
            //Buffer to hold data read from the input stream.
            final byte[] buffer = new byte[1024];
            //Continuously read from the input stream and write to the output stream until there is no more data to read.
            //For each chunk of data read (length), it writes that data to the output stream (ostrm_).
            for (int length = 0; (length = istrm_.read(buffer)) != -1; ) {
                ostrm_.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
