package blater.r88.machine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryTest {
    Memory memory = new Memory();

    @Test
    void poke() {
        // given
        short valueToStore = 255;
        // when I poke it into mem
        memory.poke(0, valueToStore);

        // then right value put in right place
        assertEquals(valueToStore, memory.peek(0));
    }

    @Test
    void pokeSmallWord() {
        // given a Word with a high byte of 0
        short valueToStore = 0x00FF;

        // when I poke it into mem at address 0
        memory.pokeWord(0, valueToStore);

        // then low byte FF is in address 0
        assertEquals(0xFF, memory.peek(0));

        // and high byte 00 is in address 1
        assertEquals(0, memory.peek(1));
    }

    @Test
    void pokeLargeWord() {
        // given a Word with a high byte of 1 and low byte of 0
        short valueToStore = 0x0100;

        // when I poke it into mem at address 0
        memory.pokeWord(0, valueToStore);

        // then low byte 00 is in address 0
        assertEquals(0, memory.peek(0));

        // and high byte 01 is in address 1
        assertEquals(0x01, memory.peek(1));
    }


    @Test
    void peek() {
    }

    @Test
    void peekWord() {
    }
}