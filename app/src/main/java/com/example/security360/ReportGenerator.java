package com.example.security360;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;
import java.util.Map;

public class ReportGenerator extends AppCompatActivity {

    private static final String TAG = "ReportGeneratorActivity";
    private TextView reportTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_generator);

        // Initialize TextView
        reportTextView = findViewById(R.id.reportTextView);

        // Call method to generate and display reports
        generateReport();
    }

    private void generateReport() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reportsRef = db.collection("reports");

        Query reportsQuery = reportsRef.orderBy("timestamp", Query.Direction.DESCENDING);

        reportsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> reports = task.getResult().getDocuments();
                    processReports(reports);
                } else {
                    Log.e(TAG, "Error getting documents: " + task.getException());
                }
            }
        });
    }

    private void processReports(List<DocumentSnapshot> reports) {
        StringBuilder report = new StringBuilder();

        for (DocumentSnapshot reportDoc : reports) {
            Map<String, Object> reportData = reportDoc.getData();
            report.append("Report ID: ").append(reportDoc.getId()).append("\n");
            report.append("Timestamp: ").append(reportData.get("timestamp")).append("\n");

            // Append location if available
            if (reportData.containsKey("location")) {
                report.append("Location: ").append(reportData.get("location")).append("\n");
            }

            // Append message if available
            if (reportData.containsKey("message")) {
                report.append("Message: ").append(reportData.get("message")).append("\n");
            }

            // Append triggered_by if available
            if (reportData.containsKey("triggered_by")) {
                report.append("Triggered by: ").append(reportData.get("triggered_by")).append("\n");
            }

            if (reportData.containsKey("event_type")) {
                // If the document contains "event_type" field, append its value
                report.append("Type of event: ").append(reportData.get("event_type")).append("\n");
            }
            report.append("\n");
        }

        // Update TextView with the report information
        reportTextView.setText(report.toString());
    }
}

