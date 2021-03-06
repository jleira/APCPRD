
package com.jamar.credit.service.creditbureau;

import java.io.StringReader;
import java.util.Date;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.swing.BorderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jamar.apc.persistence.adapter.impl.APCPersistenseAdapter;
import com.jamar.apc.persistence.adapter.port.IAPCPersistenseAdapter;
import com.jamar.apc.persistence.entities.Cenclientesconsultar;
import com.jamar.apc.persistence.port.InformeRepository;
import com.jamar.apc.util.Constants;
import com.jamar.commons.common.ResponseFailureType;
import com.jamar.credit.schema.creditbureau.FindCreditHistoryResponseType;
import com.jamar.exception.PersistenceException;
//import com.jamar.datacredito.business.port.AuditoriaBusiness;
import com.ssasis.apc.encrypt.EncryptDecrypt;
import com.ssasis.apc.encrypt.EncryptDecryptException;
import com.sun.xml.internal.ws.client.ClientTransportException;

import generated.Solicitud;
import https.www_apc_com_pa.webservices.classicscoreplusservice.ClassicScorePlusService;
import https.www_apc_com_pa.webservices.classicscoreplusservice.ClassicScorePlusServiceSoap;

/**
 * This class was generated by the JAX-WS RI. Oracle JAX-WS 2.1.5 Generated
 * source version: 2.1
 * 
 */
@WebService(portName = "CreditBureauPort", serviceName = "CreditBureauService", targetNamespace = "http://www.jamar.com/Credit/Service/CreditBureau", wsdlLocation = "/wsdls/CreditBureauService.wsdl", endpointInterface = "com.jamar.credit.service.creditbureau.CreditBureauInterface")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@HandlerChain(file = "handlers.xml")
@Configuration
@PropertySource("classpath:/META-INF/messages.properties")
public class CreditBureauService_CreditBureauPortImpl extends SpringBeanAutowiringSupport
		implements CreditBureauInterface {
	@Resource
	private WebServiceContext ctx;

	@Autowired
	private Environment env;

	@Autowired
	InformeRepository informeRepository;

	@Autowired
	private IAPCPersistenseAdapter responseAdapter;

	static final Logger errorLog = Logger.getLogger("errorDataCreditoLogger");

	public CreditBureauService_CreditBureauPortImpl() {
	}

	/**
	 * 
	 * @param body
	 * @return returns com.jamar.credit.schema.creditbureau.
	 *         FindCreditHistoryResponseType
	 * @throws MandatoryDataMissingFault
	 * @throws ExternalSystemNotAvailableFault
	 * @throws BusinessFault
	 * @throws IllegalUsageFault
	 * @throws ReferenceDataMissingFault
	 * @throws InternalSystemFault
	 * @throws DataNotFoundFault
	 */
	public FindCreditHistoryResponseType findCreditHistory(Solicitud body)
			throws BusinessFault, DataNotFoundFault, ExternalSystemNotAvailableFault, IllegalUsageFault,
			InternalSystemFault, MandatoryDataMissingFault, ReferenceDataMissingFault {

		boolean resultado = false;
		String usuario = (String) ctx.getMessageContext().get("usuario");
		String origen = (String) ctx.getMessageContext().get("origen");
		Date fechaEntrada = new Date();
		FindCreditHistoryResponseType response = null;
		Cenclientesconsultar consulta = null;
		ClassicScorePlusService service = new ClassicScorePlusService();
		String trustStorePath = "";
		String trustStorePassword = "";
		try {
			System.out.println("inicio todo remodelado");

//			trustStorePath = env.getProperty(Constants.APC_TRUST_STORE_LOCAL);
//			trustStorePath =env.getProperty(Constants.APC_TRUST_STORE);
//			trustStorePassword = "DemoTrustKeyStorePassPhrase";
//			System.setProperty("javax.net.ssl.trustStore", trustStorePath);
//			System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
//			System.setProperty("javax.net.ssl.trustAnchors", trustStorePath);

			
/*			System.setProperty("javax.net.ssl.trustStore", "/Users/jleira/u01/jks/mueblesjamar.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "Jamar01");
			System.setProperty("javax.net.ssl.trustAnchors", "/Users/jleira/u01/jks/mueblesjamar.jks");
*/			
			System.setProperty("javax.net.ssl.keyStore", "/Users/jleira/mueblesjamar.jks");
//			System.setProperty("javax.net.ssl.keyStore", "/u01/ssl/certificate/mueblesjamar.jks");			
			System.setProperty("javax.net.ssl.keyStoreType", "jks");
			System.setProperty("javax.net.ssl.keyStorePassword", "Jamar01");


//			System.setProperty("javax.net.ssl.trustStoreType", "JKS");										
			EncryptDecrypt encrypt = new EncryptDecrypt();

			ClassicScorePlusServiceSoap servicio = service.getClassicScorePlusServiceSoap();
			BindingProvider provider = (BindingProvider) servicio;
			provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					env.getProperty(Constants.APC_URL));
			String result = "";

			System.out.println(env.getProperty(Constants.APC_URL));
			try {
				
				System.out.println("inicio apc");
				result = servicio.getScore(encrypt.encrypt(body.getSolicitud().getUsuarioConsulta()),
						encrypt.encrypt(body.getSolicitud().getClaveConsulta()),
						encrypt.encrypt(body.getSolicitud().getIdentificacion()),
						encrypt.encrypt(body.getSolicitud().getTipoIdentificacion()),
						encrypt.encrypt(body.getSolicitud().getProducto()));
				System.out.println("finalizo apc");

				
				consulta = responseAdapter.getConsultaFromServiceResponse(result);
				consulta.setFechaentrada(fechaEntrada);
				consulta.setFechasalida(new Date());
				consulta.setTipoidentificacion(Short.parseShort(body.getSolicitud().getTipoIdentificacion()));
				consulta.setControl(Short.parseShort("1"));
				consulta.setNumeroidentificacion(body.getSolicitud().getIdentificacion());
				consulta.setCodigo("2999");
				consulta.setPrimerapellido(body.getSolicitud().getPrimerApellido());
				consulta.setUsuario(usuario);
				consulta.setOrigen(origen);
				informeRepository.save(consulta);
				response = new FindCreditHistoryResponseType();
				response.setAnswer(true);
//				ResponseFailureType failureType = new ResponseFailureType();
//				failureType.setErrorCode(result);
//				response.setResponseFailure(failureType);

			} catch (PersistenceException e) {
				e.printStackTrace();
			} catch (Exception e) {
				consulta = new Cenclientesconsultar();
				consulta.setFechaentrada(fechaEntrada);
				consulta.setFechasalida(new Date());
				consulta.setTipoidentificacion(Short.parseShort(body.getSolicitud().getTipoIdentificacion()));
				consulta.setControl(Short.parseShort("0"));
				consulta.setNumeroidentificacion(body.getSolicitud().getIdentificacion());
				consulta.setCodigo("2999");
				consulta.setPrimerapellido(body.getSolicitud().getPrimerApellido());
				consulta.setUsuario(usuario);
				consulta.setOrigen(origen);
				try {
					informeRepository.save(consulta);
				} catch (PersistenceException e1) {
					e1.printStackTrace();
				}

				response = new FindCreditHistoryResponseType();
				response.setAnswer(false);
				ResponseFailureType failureType = new ResponseFailureType();
				failureType.setErrorCode(e.getMessage());
				response.setResponseFailure(failureType);

			}

		} catch (EncryptDecryptException e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

		return response;
	}

}
