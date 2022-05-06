package br.senai.sp.guia.guiabalada.util;

import java.io.IOException;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class FirebaseUtil {
	//variavel para guardar as credencias de acesso
	private Credentials credenciais;
	//variaves para acessar e manipular o storeg
	private Storage storege;
	//constante para o nome do bucket
	private final String BUCKET_NAME ="guia-sara.appspot.com";
	//constante para o prefixo da URL
	private final String PREFIX ="https://firebasestorage.googleapis.com/v0/b/"+BUCKET_NAME+"/o/";
	//constante para sufixo da URL
	private final String SUFFIX ="?alt=media";
	//constante para URL
	private final String DOWNLOND_URL= PREFIX +"%s" + SUFFIX;
	
	public FirebaseUtil() {
		//acessar o arquivo json com a chave privada
		Resource resource = new ClassPathResource("chavefirebase.json");
		//gera uma credencial no Firebase atraves da chave do arquivo	
		try {
			credenciais = GoogleCredentials.fromStream(resource.getInputStream());
			//cria o storege para manipular os dados no Firebase
			storege =  StorageOptions.newBuilder().setCredentials(credenciais).build().getService();
		} catch (IOException e) {
			throw new RuntimeErrorException(null, e.getMessage());
			
		}		
	}
	
	//metodo para extrair a extensão do arquivo
	private String getExtensao(String nomeArquivo) {
		//extrai o trecho do arquivo onde esta a extensao
		return nomeArquivo.substring(nomeArquivo.lastIndexOf('.'));
	}
	
	//metodo que faz o upload
	public String upload(MultipartFile arquivo) throws IOException {
		//gera um nome aleatorio para o arquivo
		String nomeArquivo = UUID.randomUUID().toString() + getExtensao(arquivo.getOriginalFilename());
		//criar um blobId através do nome gerado para o arquivo
		BlobId blobId = BlobId.of(BUCKET_NAME, nomeArquivo);
		//criar um BlobInfo através do BlobId 
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
		//gravar o blobInfo no Storege passando os Bytes do arquivo
		storege.create(blobInfo, arquivo.getBytes());
		//retorna a URL do arquivo gerado no Storege
		return String.format(DOWNLOND_URL, nomeArquivo);
	}
	
	//método que exclui o arquivo do storege
	public void deletar(String nomeArquivo) {
		//retirar o prefixo e o sufixo da String
		nomeArquivo = nomeArquivo.replace(PREFIX, "").replace(SUFFIX, "");
		//obter um Blob atraves do nome
		Blob blob= storege.get(BlobId.of(BUCKET_NAME, nomeArquivo));
		//deleta atraves do blob
		storege.delete(blob.getBlobId());
	}
	
	
}
