package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class DefaultAllocator implements Allocator {
    private static final int AVAILABLE_EXTRA_CAPACITY = 100;
    private int allocatedCount;
    private Allocation[] availableAllocations;
    private int availableCount;
    private final int individualAllocationSize;
    private final byte[] initialAllocationBlock;
    private final Allocation[] singleAllocationReleaseHolder;
    private int targetBufferSize;
    private final boolean trimOnReset;

    public DefaultAllocator(boolean trimOnReset, int individualAllocationSize) {
        this(trimOnReset, individualAllocationSize, 0);
    }

    public DefaultAllocator(boolean trimOnReset, int individualAllocationSize, int initialAllocationCount) {
        boolean z = false;
        Assertions.checkArgument(individualAllocationSize > 0);
        Assertions.checkArgument(initialAllocationCount >= 0 ? true : z);
        this.trimOnReset = trimOnReset;
        this.individualAllocationSize = individualAllocationSize;
        this.availableCount = initialAllocationCount;
        this.availableAllocations = new Allocation[initialAllocationCount + 100];
        if (initialAllocationCount > 0) {
            this.initialAllocationBlock = new byte[initialAllocationCount * individualAllocationSize];
            for (int i = 0; i < initialAllocationCount; i++) {
                int allocationOffset = i * individualAllocationSize;
                this.availableAllocations[i] = new Allocation(this.initialAllocationBlock, allocationOffset);
            }
        } else {
            this.initialAllocationBlock = null;
        }
        this.singleAllocationReleaseHolder = new Allocation[1];
    }

    public synchronized void reset() {
        if (this.trimOnReset) {
            setTargetBufferSize(0);
        }
    }

    public synchronized void setTargetBufferSize(int targetBufferSize) {
        boolean targetBufferSizeReduced = targetBufferSize < this.targetBufferSize;
        this.targetBufferSize = targetBufferSize;
        if (targetBufferSizeReduced) {
            trim();
        }
    }

    @Override // com.google.android.exoplayer2.upstream.Allocator
    public synchronized Allocation allocate() {
        Allocation allocation;
        this.allocatedCount++;
        int i = this.availableCount;
        if (i > 0) {
            Allocation[] allocationArr = this.availableAllocations;
            int i2 = i - 1;
            this.availableCount = i2;
            allocation = allocationArr[i2];
            allocationArr[i2] = null;
        } else {
            allocation = new Allocation(new byte[this.individualAllocationSize], 0);
        }
        return allocation;
    }

    @Override // com.google.android.exoplayer2.upstream.Allocator
    public synchronized void release(Allocation allocation) {
        Allocation[] allocationArr = this.singleAllocationReleaseHolder;
        allocationArr[0] = allocation;
        release(allocationArr);
    }

    @Override // com.google.android.exoplayer2.upstream.Allocator
    public synchronized void release(Allocation[] allocations) {
        int i = this.availableCount;
        int length = allocations.length + i;
        Allocation[] allocationArr = this.availableAllocations;
        if (length >= allocationArr.length) {
            this.availableAllocations = (Allocation[]) Arrays.copyOf(allocationArr, Math.max(allocationArr.length * 2, i + allocations.length));
        }
        for (Allocation allocation : allocations) {
            Allocation[] allocationArr2 = this.availableAllocations;
            int i2 = this.availableCount;
            this.availableCount = i2 + 1;
            allocationArr2[i2] = allocation;
        }
        this.allocatedCount -= allocations.length;
        notifyAll();
    }

    @Override // com.google.android.exoplayer2.upstream.Allocator
    public synchronized void trim() {
        int targetAllocationCount = Util.ceilDivide(this.targetBufferSize, this.individualAllocationSize);
        int targetAvailableCount = Math.max(0, targetAllocationCount - this.allocatedCount);
        int i = this.availableCount;
        if (targetAvailableCount >= i) {
            return;
        }
        if (this.initialAllocationBlock != null) {
            int lowIndex = 0;
            int highIndex = i - 1;
            while (lowIndex <= highIndex) {
                Allocation lowAllocation = this.availableAllocations[lowIndex];
                if (lowAllocation.data == this.initialAllocationBlock) {
                    lowIndex++;
                } else {
                    Allocation highAllocation = this.availableAllocations[highIndex];
                    if (highAllocation.data != this.initialAllocationBlock) {
                        highIndex--;
                    } else {
                        Allocation[] allocationArr = this.availableAllocations;
                        allocationArr[lowIndex] = highAllocation;
                        allocationArr[highIndex] = lowAllocation;
                        highIndex--;
                        lowIndex++;
                    }
                }
            }
            targetAvailableCount = Math.max(targetAvailableCount, lowIndex);
            if (targetAvailableCount >= this.availableCount) {
                return;
            }
        }
        Arrays.fill(this.availableAllocations, targetAvailableCount, this.availableCount, (Object) null);
        this.availableCount = targetAvailableCount;
    }

    @Override // com.google.android.exoplayer2.upstream.Allocator
    public synchronized int getTotalBytesAllocated() {
        return this.allocatedCount * this.individualAllocationSize;
    }

    @Override // com.google.android.exoplayer2.upstream.Allocator
    public int getIndividualAllocationLength() {
        return this.individualAllocationSize;
    }
}
