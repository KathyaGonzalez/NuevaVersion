package kathya.bilena.springbootbackend;

import kathya.bilena.springbootbackend.controller.UsuarioController;
import kathya.bilena.springbootbackend.model.Usuario;
import kathya.bilena.springbootbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@SpringBootApplication
public class SpringbootBackendApplication extends Thread implements CommandLineRunner{

	@GetMapping("/MostrarDatos")
	public String getMessage() {
		String mensaje = "|| ";
		for (int i = 0; i < usuarioController.getAllUsuarios().size(); i++){
			String nombre = usuarioController.getAllUsuarios().get(i).getNombre();
			String espacio = "          ";
			if (nombre.length() < 20) {
				for (int j = 0; j < (20 - nombre.length()); j++){
					espacio += " ";
				}
			}
			String correo = usuarioController.getAllUsuarios().get(i).getCorreo();
			String espacioCorreo = "          ";
			if (correo.length() < 30) {
				for (int j = 0; j < (30 - correo.length()); j++){
					espacioCorreo += " ";
				}
			}
			mensaje += "Nombre: " + nombre + espacio + " Correo: " + correo + espacioCorreo + " Empleo: " + usuarioController.getAllUsuarios().get(i).getEmpleo() + " || ";
		}
		return mensaje;
	}

	@GetMapping("/CargarDatos")
	public String getMessag() {
		try{
			int peticiones = 4;
			int ronda = 1;
			while (ronda > 0){
				ronda -= 1;
				URL url = new URL("https://random-data-api.com/api/v2/users?size=" + peticiones);
				HttpURLConnection conn= (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.connect();
				int responseCode = conn.getResponseCode();
				if (responseCode != 200){
					throw new RuntimeException("Ocurrió un error: "+ responseCode);
				}else{
					StringBuilder informationString = new StringBuilder();
					Scanner scanner = new Scanner(url.openStream());
					while(scanner.hasNext()){
						informationString.append(scanner.nextLine());
					}
					scanner.close();
					JSONArray jsonArray = new JSONArray(informationString.toString());
					for(int x=0; x< peticiones; x++){
						JSONObject jsonObject = jsonArray.getJSONObject(x);
						String nombre = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
						String correo = jsonObject.getString("email");
						String empleo = jsonObject.getJSONObject("employment").getString("title");
						Usuario usuario = new Usuario();
						usuario.setNombre(nombre);
						usuario.setCorreo(correo);
						usuario.setEmpleo(empleo);
						usuarioRepository.save(usuario);
						Thread.sleep(500);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "Cargando datos";
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootBackendApplication.class, args);
	}

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioController usuarioController;
	@Override
	public void run(String... args) throws Exception{
	}
}