package PetSitters.serviceDTO;

public class DTOCity implements DTO {

    String city;

    public DTOCity() {
    }

    public DTOCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (char c : city.toCharArray()) {
            result = result * 256 + c;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        DTOCity dtoCity = (DTOCity) o;
        return city.equals(dtoCity.city);
    }
}
