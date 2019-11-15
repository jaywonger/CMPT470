/*
 * Author: C M Khaled Saifullah
 * nsid: cms500
 * Assignment for CMPT470/816
 */

package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class APIMethod {
    private String name;
    private String fileName;
    private int lineNumber;
    private List<String> context;
    private String FQN;

    public APIMethod() {
        context = new ArrayList<>();
    }

    public APIMethod(String name, String fileName, int lineNumber, String FQN) {
        this.name = name;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.FQN = FQN;
        context = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public List<String> getContext() {
        return context;
    }

    public String getContextInString() {
        StringBuilder contextString = new StringBuilder();
        for(String eachToken:context)
            contextString.append(eachToken).append(" ");
        return contextString.toString().trim();
    }

    public void setContext(List<String> context) {
        this.context = context;
    }

    public String getFQN() {
        return FQN;
    }

    public void setFQN(String FQN) {
        this.FQN = FQN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof APIMethod)) return false;
        APIMethod that = (APIMethod) o;
        return getLineNumber() == that.getLineNumber() &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getFileName(), that.getFileName()) &&
                Objects.equals(getContext(), that.getContext()) &&
                Objects.equals(getFQN(), that.getFQN());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getFileName(), getLineNumber(), getContext(), getFQN());
    }

    public String toString(){
        return "Name :"+name
                +"\nFileName: "+fileName
                +"\nLineNumber: "+lineNumber
                +"\nContext: "+ context.toString()
                +"\nActualFQN: "+ FQN
                +"\n";
    }
}
