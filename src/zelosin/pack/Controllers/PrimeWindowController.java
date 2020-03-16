package zelosin.pack.Controllers;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import zelosin.pack.Data.GFGCell;
import zelosin.pack.Data.GFGException;
import zelosin.pack.Services.UDP.EchoServer;
import zelosin.pack.base.Main;

import java.io.*;
import java.net.SocketAddress;
import java.util.ArrayList;


public class PrimeWindowController {
    @FXML
    public Button dAddButton;
    @FXML
    public Button dDeleteButton;
    @FXML
    public Button dResolveButton;

    @FXML
    public TextField dUpBorderField;
    @FXML
    public TextField dDownBorderField;
    @FXML
    public TextField dAccuracyField;

    @FXML
    public TableView<GFGCell> dPrimeTable;

    @FXML
    public TableColumn<GFGCell, Float> dUpColumn;
    @FXML
    public TableColumn<GFGCell, Float> dDownColumn;
    @FXML
    public TableColumn<GFGCell, Float> dAccuracyColumn;
    @FXML
    public TableColumn<GFGCell, Float> dResolveColumn;


    @FXML
    public ListView<SocketAddress> dIPList;

    public ObservableList<GFGCell> mGFGTableList = FXCollections.observableArrayList();
    public ArrayList<GFGCell> mGFGList = new ArrayList<>();
    private final String mFileName = "GFG.txt";
    private ByteArrayOutputStream byteArrayOutputStream;
    private static GFGCell mSelectedCell;

    private void initializeListeners(){

        dPrimeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GFGCell>() {
            @Override
            public void changed(ObservableValue<? extends GFGCell> observableValue, GFGCell gfgCell, GFGCell t1) {
                GFGCell mSelectedCell = dPrimeTable.getSelectionModel().getSelectedItem();
                if(mSelectedCell != null){
                    dUpBorderField.setText(Float.toString(mSelectedCell.getUpBorder()));
                    dDownBorderField.setText(Float.toString(mSelectedCell.getDownBorder()));
                    dAccuracyField.setText(Float.toString(mSelectedCell.getAccuracy()));
                }
            }
        });

        dUpBorderField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null) {
                GFGCell mSelectedCell = dPrimeTable.getSelectionModel().getSelectedItem();
                if (mSelectedCell != null) {
                    try{
                        Float mFloatValue = Float.parseFloat(newValue);
                        if(mFloatValue < 0.000001 || mFloatValue > 1000000)
                            throw new GFGException("Incorrect value");
                        mSelectedCell.setUpBorder(mFloatValue);
                    }
                    catch(GFGException | NumberFormatException e){
                        createAlert();
                    } finally {
                        dPrimeTable.refresh();
                    }
                }
            }
        });
        dDownBorderField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null) {
                GFGCell mSelectedCell = dPrimeTable.getSelectionModel().getSelectedItem();
                if (mSelectedCell != null) {
                    try{
                        Float mFloatValue = Float.parseFloat(newValue);
                        if(mFloatValue < 0.000001 || mFloatValue > 1000000)
                            throw new GFGException("Incorrect value");
                        mSelectedCell.setDownBorder(mFloatValue);
                    }
                    catch(GFGException | NumberFormatException e){
                        createAlert();
                    } finally {
                        dPrimeTable.refresh();
                    }
                }
            }
        });
        dAccuracyField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null) {
                GFGCell mSelectedCell = dPrimeTable.getSelectionModel().getSelectedItem();
                if (mSelectedCell != null) {
                    try{
                        Float mFloatValue = Float.parseFloat(newValue);
                        if(mFloatValue < 0.000001 || mFloatValue > 1000000)
                            throw new GFGException("Incorrect value");
                        mSelectedCell.setAccuracy(mFloatValue);
                    }
                    catch(GFGException | NumberFormatException e){
                        createAlert();
                    } finally {
                        dPrimeTable.refresh();
                    }
                }
            }
        });
    }

    public void refreshTable(){
        dPrimeTable.refresh();
    }



    private void createAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setContentText("Incorrect value");
        alert.showAndWait();
    }

    public void initialize(){
        dIPList.setItems(EchoServer.mClientsList);
        dUpColumn.setCellValueFactory(new PropertyValueFactory<GFGCell, Float>("UpBorder"));
        dDownColumn.setCellValueFactory(new PropertyValueFactory<GFGCell, Float>("DownBorder"));
        dAccuracyColumn.setCellValueFactory(new PropertyValueFactory<GFGCell, Float>("Accuracy"));
        dResolveColumn.setCellValueFactory(new PropertyValueFactory<GFGCell, Float>("Result"));
        dPrimeTable.setItems(mGFGTableList);
        initializeListeners();
    }

    public void onActionAdd(MouseEvent mouseEvent) {
        GFGCell mNewGFGCell = new GFGCell(1, 1, 1);
        mGFGTableList.add(mNewGFGCell);
        mGFGList.add(mNewGFGCell);
    }

    public void onActionDelete(MouseEvent mouseEvent) {
        GFGCell mSelectedCell = dPrimeTable.getSelectionModel().getSelectedItem();
        if(mSelectedCell != null){
            dUpBorderField.setText(null);
            dDownBorderField.setText(null);
            dAccuracyField.setText(null);
            mGFGTableList.remove(mSelectedCell);
            mGFGList.add(mSelectedCell);
        }
    }

    public void onActionResolve(MouseEvent mouseEvent) {
        mSelectedCell = dPrimeTable.getSelectionModel().getSelectedItem();
        if(mSelectedCell != null){
            System.out.println(mSelectedCell);
            EchoServer.mCurrentGFGCell = mSelectedCell;
            EchoServer.stopClientsDetect();
            dPrimeTable.refresh();
        }
    }

    public void onActionClear(MouseEvent mouseEvent) {
        dUpBorderField.setText(null);
        dDownBorderField.setText(null);
        dAccuracyField.setText(null);
        dPrimeTable.getItems().clear();
    }

    public void onActionFill(MouseEvent mouseEvent) {
        dUpBorderField.setText(null);
        dDownBorderField.setText(null);
        dAccuracyField.setText(null);
        dPrimeTable.getItems().clear();
        dPrimeTable.getItems().addAll(mGFGList);
    }

    public void onClickedOutputText(MouseEvent mouseEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File mFileToSave = fileChooser.showSaveDialog(Main.primaryStage);
        if(mFileToSave == null)
            return;

        dUpBorderField.setText(null);
        dDownBorderField.setText(null);
        dAccuracyField.setText(null);

        ArrayList<GFGCell> mListForSerialize = new ArrayList<>(mGFGTableList);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mFileToSave.getAbsolutePath())))
        {
            oos.writeObject(mListForSerialize);
        }
        mGFGTableList.clear();
    }

    public void onClickedInputText(MouseEvent mouseEvent) throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File mFileToSave = fileChooser.showOpenDialog(Main.primaryStage);
        if(mFileToSave == null)
            return;


        mGFGTableList.clear();
        ArrayList<GFGCell> mListForSerialize = new ArrayList<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mFileToSave.getAbsolutePath())))
        {
            mListForSerialize =((ArrayList<GFGCell>)ois.readObject());
            dPrimeTable.refresh();
        }
        mGFGTableList.addAll(mListForSerialize);
    }

    public void onClickedOutputStream(MouseEvent mouseEvent) throws IOException {


       /* PrintWriter pw = new PrintWriter(new FileOutputStream(mFileToSave.getAbsolutePath()));
        for (GFGCell mTempCell : mGFGTableList)
            pw.println(mTempCell.toString());
        pw.close();
        mGFGTableList.clear();*/


       if(mGFGTableList.isEmpty())
            return;
        ArrayList<GFGCell> mListForSerialize = new ArrayList<>(mGFGTableList);
        byteArrayOutputStream  = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream ) ;
        objectOutputStream.writeObject(mListForSerialize);
        objectOutputStream.flush();
        mGFGTableList.clear();
    }

    public void onClickedInputStream(MouseEvent mouseEvent) throws IOException, ClassNotFoundException {
        ObjectInputStream objectOutputStream = new ObjectInputStream(
                new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        ArrayList<GFGCell> mListForSerialize = (ArrayList<GFGCell>) objectOutputStream.readObject();
        mGFGTableList.addAll(mListForSerialize);
        objectOutputStream.close();
    }
}






























