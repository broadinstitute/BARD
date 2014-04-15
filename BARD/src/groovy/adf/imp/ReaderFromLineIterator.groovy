package adf.imp
/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/10/14
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
class ReaderFromLineIterator extends Reader  {
    Iterator<String> iterator;
    char[] next;
    int offset;
    boolean reachedEnd = false;
    char[] terminator;

    public ReaderFromLineIterator(Iterator<String> iterator, String terminator) {
        this.iterator = iterator;
        this.terminator = terminator.getChars();
        populateNext();
    }

    void populateNext() {
        if(!iterator.hasNext()) {
            reachedEnd = true;

            // clear out the locals because they should never get used again
            offset = -1;
            next = null;
            iterator = null;
            return;
        }

        String nextString = iterator.next();
        next = new char[nextString.length()+terminator.length];
        nextString.getChars(0, nextString.length(), next, 0)
        System.arraycopy(terminator, 0, next, nextString.length(), terminator.length)
        offset = 0;
    }

    @Override
    int read(char[] dest, int offset, int length) throws IOException {
        int charsCopied = 0;

        while(true) {
            if(reachedEnd) {
                if(charsCopied == 0)
                    charsCopied = -1;
                break;
            }

            int copyLen = Math.min(next.length-this.offset, length-charsCopied);
            System.arraycopy(next, this.offset, dest, offset, copyLen);
            offset += copyLen;
            this.offset += copyLen;
            charsCopied += copyLen;

            // did we reach the end of the current buffer
            if(this.offset == next.length) {
                populateNext();
            }

            // did we successfully reach the length
            if(length == charsCopied)
                break;
        }

        return charsCopied;
    }

    @Override
    void close() throws IOException {
        this.iterator = null;
    }
}

