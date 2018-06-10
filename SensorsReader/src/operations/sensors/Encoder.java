package operations.sensors;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Encoder")
public class Encoder extends Sensor {
	private int variable = 69;

	public int getVariable() {
		return variable;
	}

	public void setVariable(int i) {
		variable = i;
	}

	public Encoder() {
	}

}
