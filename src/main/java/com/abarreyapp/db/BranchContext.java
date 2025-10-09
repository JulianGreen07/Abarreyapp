package com.abarreyapp.db;

/**
 * Simple in-memory branch context. Default branch id = 1.
 */
public class BranchContext {
    private static int currentBranchId = 1;

    public static int getCurrentBranchId() {
        return currentBranchId;
    }

    public static void setCurrentBranchId(int id) {
        currentBranchId = id;
    }
}
