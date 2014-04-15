package adf.imp

import adf.imp.ReaderFromLineIterator
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/10/14
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
class ReaderFromLineIteratorUnitSpec extends Specification {
    def testReadSubLine() {
        setup:
        ReaderFromLineIterator r = new ReaderFromLineIterator(["abc"].iterator(), "");
        char[] buf= new char[4];
        int c;

        when:
        c = r.read(buf, 0, 2)

        then:
        buf[0] == 'a'
        buf[1] == 'b'
        c == 2
    }

    def testTerminator() {
        setup:
        ReaderFromLineIterator r = new ReaderFromLineIterator(["a","b"].iterator(), "\n");
        char[] buf= new char[4];
        int c;

        when:
        c = r.read(buf, 0, 4)
        String s = new String(buf)

        then:
        c == 4
        s == "a\nb\n"
    }

    def testNonAlignedReads() {
        setup:
        ReaderFromLineIterator r = new ReaderFromLineIterator(["a", "b", "c"].iterator(), "");
        char[] buf= new char[4];
        int c;

        when:
        c = r.read(buf, 0, 2)

        then:
        buf[0] == 'a'
        buf[1] == 'b'
        c == 2

        when:
        c = r.read(buf, 2, 2)

        then:
        buf[2] == 'c'
        c == 1

        when:
        c = r.read(buf, 0, 1)

        then:
        c == 0
    }

    def testBoundaryAlignedReads() {
        setup:
        ReaderFromLineIterator r = new ReaderFromLineIterator(["a", "b", "c"].iterator(), "");
        char[] buf= new char[3];
        int c;

        when:
        c = r.read(buf, 0, 1)

        then:
        buf[0] == 'a'
        c == 1

        when:
        c = r.read(buf, 1, 1)

        then:
        buf[1] == 'b'
        c == 1

        when:
        c = r.read(buf, 2, 1)

        then:
        buf[2] == 'c'
        c == 1

        when:
        c = r.read(buf, 0, 1)

        then:
        c == 0
    }
}
