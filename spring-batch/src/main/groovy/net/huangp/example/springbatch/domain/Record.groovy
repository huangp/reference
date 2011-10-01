package net.huangp.example.springbatch.domain

class Record implements Comparable<Record> {
    String messageType
    String sendOrReceive
    String number
    Date timestamp
    String content

    int compareTo(Record other) {
        timestamp.compareTo(other.timestamp)
    }
}