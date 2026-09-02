package com.mira.combat.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatNumbersTest {
    @Test
    void clampsIntegers() {
        assertEquals(20, CombatNumbers.clamp(25, 0, 20));
        assertEquals(0, CombatNumbers.clamp(-5, 0, 20));
        assertEquals(10, CombatNumbers.clamp(10, 0, 20));
    }

    @Test
    void clampsDecimals() {
        assertEquals(2.0D, CombatNumbers.clamp(5.0D, 0.0D, 2.0D));
        assertEquals(0.0D, CombatNumbers.clamp(-1.0D, 0.0D, 2.0D));
        assertEquals(0.4D, CombatNumbers.clamp(0.4D, 0.0D, 2.0D));
    }
}
