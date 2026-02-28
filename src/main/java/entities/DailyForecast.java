package entities;

import java.time.LocalDate;
import utils.WeatherCodeLabels;

/**
 * Prévision météo pour un jour (période de travail).
 */
public class DailyForecast {
    private LocalDate date;
    private double tempMin;
    private double tempMax;
    private double precipitationMm;
    private int weatherCode;

    public DailyForecast() {}

    public DailyForecast(LocalDate date, double tempMin, double tempMax, double precipitationMm, int weatherCode) {
        this.date = date;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.precipitationMm = precipitationMm;
        this.weatherCode = weatherCode;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }

    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }

    public double getPrecipitationMm() { return precipitationMm; }
    public void setPrecipitationMm(double precipitationMm) { this.precipitationMm = precipitationMm; }

    public int getWeatherCode() { return weatherCode; }
    public void setWeatherCode(int weatherCode) { this.weatherCode = weatherCode; }

    /**
     * Description lisible du temps (code WMO).
     */
    public String getWeatherDescription() {
        return WeatherCodeLabels.label(weatherCode);
    }
}
