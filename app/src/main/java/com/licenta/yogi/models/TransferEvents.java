package com.licenta.yogi.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.web3j.tx.Transfer;

public class TransferEvents implements Parcelable {

    private String receiverAddress;
    private long amount;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public TransferEvents(String receiver, long amt) {
        this.receiverAddress    = receiver;
        this.amount     = amt;
    }
    protected TransferEvents(Parcel in) {
        receiverAddress = in.readString();
        amount = in.readLong();
    }
    public String getAddress() {
        return receiverAddress;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(receiverAddress);
        dest.writeLong(amount);
    }

    public static Creator<TransferEvents> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<TransferEvents> CREATOR = new Creator<TransferEvents>() {
        @Override
        public TransferEvents createFromParcel(Parcel in) {
            return new TransferEvents(in);
        }

        @Override
        public TransferEvents[] newArray(int size) {
            return new TransferEvents[size];
        }
    };

}
