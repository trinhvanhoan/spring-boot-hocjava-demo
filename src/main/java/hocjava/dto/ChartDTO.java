package hocjava.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

/**
 * Chart config
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartDTO {
    private String type;
    private ChartData data;
    
    /*
     * Create Chart
     */
    public static ChartDTO create(String type, List<String> labels) {
        ChartDTO dto = new ChartDTO();
        dto.setType(type);
        dto.setData(new ChartData());
        dto.getData().setLabels(labels);
        dto.getData().setDatasets(new ArrayList<>());
        return dto;
    }

    /*
     * Add dataset to chart
     */
    public ChartDTO addDataset(String label, List<? extends Object> dataValues) {
        ChartDataset dataset = new ChartDataset();
        dataset.setLabel(label);
        dataset.setData(dataValues);
        this.data.getDatasets().add(dataset);
        return this;
    }
    
    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ChartData {
    private List<String> labels;
    private List<ChartDataset> datasets;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ChartDataset {
    private String label;
    private List<? extends Object> data;

}