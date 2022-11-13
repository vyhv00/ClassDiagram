/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classDiagram.fileCreatedListener;

/**
 *
 * @author vojta
 */
public interface FileSubject {
    public void notifyListeners();
    public void addListener(FileListener listener);
    public void removeListener(FileListener listener);
}
