package skenav.core.security;

import org.shredzone.acme4j.*;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;
import skenav.core.Cache;

import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class CertificateManagement {
	public static void testthings() {
		//KeyPair certkeypair = generateKeyPair();
		//saveKeypairToFile(certkeypair);
		KeyPair accoutkeypair = readKeyPairFile();
		Session session = createSession();
		URL accounturl = getAccountURL(accoutkeypair, session);





	}

	/*public static Certificate orderCert() {
		KeyPair kp = readKeyPairFile();
		Account account = login(kp);
		Order order = null;
		String domain = Cache.INSTANCE.getDomain();
		try {
			order = account.newOrder()
					.domains(domain, "*." + domain)
					.create();
		} catch (AcmeException e) {
			e.printStackTrace();
		}
		for (Authorization auth : order.getAuthorizations()) {
			if (auth.getStatus() == Status.PENDING) {
				try {
					processAuthorization(auth);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}



		CSRBuilder csrb = new CSRBuilder();
		csrb.addDomains(domain, "*." + domain);
		try {
			csrb.sign(kp);
			byte[] csr = csrb.getEncoded();
			order.execute(csr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AcmeException e) {
			e.printStackTrace();
		}

		while (order.getStatus() != Status.VALID || order.getStatus() != Status.INVALID) {
			try {
				Thread.sleep(3000L);
				order.update();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (AcmeException e) {
				throw new RuntimeException(e);
			}

		}
		//TODO: figure out how to write certificate chains to the filesystem
		if (order.getStatus() == Status.VALID) {
			List<X509Certificate> chain = order.getCertificate().getCertificateChain();
			Certificate cert = order.getCertificate();
			try (FileWriter fw = new FileWriter("cert-chain.crt")) {
				cert.writeCertificate(fw, chain);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else return null;

	}*/

	public static Session createSession() {
		String caurl = "acme://letsencrypt.org/staging";
		Session session = new Session(caurl);
		Metadata metadata = null;
		try {
			metadata = session.getMetadata();
		} catch (AcmeException e) {
			throw new RuntimeException(e);
		}
		URL website = metadata.getWebsite();
		System.out.println("website is" + website);
		return session;
	}

	public static KeyPair generateKeyPair () {
		KeyPair accountkeypair = KeyPairUtils.createKeyPair(2048);
		saveKeypairToFile(accountkeypair);
		return accountkeypair;
	}

	public static void saveKeypairToFile(KeyPair keypair) {
		String certdirectory = Cache.INSTANCE.getUploaddirectory() + "certificates/";
		try (FileWriter fw = new FileWriter(new File(certdirectory, "keypair.pem"))) {
			KeyPairUtils.writeKeyPair(keypair, fw);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static KeyPair readKeyPairFile() {
		String certdirectory = Cache.INSTANCE.getUploaddirectory() + "certificates/";
		try (FileReader fr = new FileReader(new File(certdirectory, "keypair.pem"))) {
			return KeyPairUtils.readKeyPair(fr);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static URL createAccount() {
		KeyPair kp = generateKeyPair();
		Session session = createSession();
		URL accountlocationurl = null;
		try {
			Account account = new AccountBuilder()
					//TODO: add email to database and front end
					.addContact(Cache.INSTANCE.getContact())
					.agreeToTermsOfService()
					.useKeyPair(kp)
					.create(session);
			accountlocationurl = account.getLocation();
		} catch (AcmeException e) {
			throw new RuntimeException(e);
		}
		return accountlocationurl;
	}

	public static Account login (KeyPair kp) {
		Session session = createSession();
		URL accounturl = getAccountURL(kp, session);
		Account account = session.login(accounturl, kp).getAccount();
		return account;
	}

	public static URL getAccountURL(KeyPair kp, Session session) {
		URL accountlocation = null;
		try {
			Account account = new AccountBuilder()
					.onlyExisting()
					.useKeyPair(kp)
					.create(session);
			accountlocation = account.getLocation();
		} catch (AcmeException e) {
			throw new RuntimeException(e);
		}
		return accountlocation;
	}

	public static void processAuthorization(Authorization auth) throws Exception {
		Http01Challenge challenge = auth.findChallenge(Http01Challenge.class);
		String domain = auth.getIdentifier().getDomain();
		String token = challenge.getToken();
		String content = challenge.getAuthorization();
		Cache.INSTANCE.setTlsAuthContent(content);
		System.out.println("domain from tls challenge is: " + domain);
		System.out.println("token from tls challenge is: " + token);
		System.out.println("content from tls challenge is: " + content);
		try {
			challenge.trigger();
		} catch (AcmeException e) {
			throw new RuntimeException(e);
		}
		while (auth.getStatus() != Status.VALID && auth.getStatus() != Status.INVALID) {
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			try {
				auth.update();
				System.out.println("checking for certificate success");
			} catch (AcmeException e) {
				throw new RuntimeException(e);
			}
		}
		if (auth.getStatus() == Status.VALID){
			return;
		}
		else {throw new Exception("http challenge failed");}
	}
	//TODO: add domain name configuration and make certs available to ip addresses
	public static void finalizeOrder() {
		KeyPair keyPair = readKeyPairFile();
		CSRBuilder csrBuilder = new CSRBuilder();
		csrBuilder.addDomains("skenav.io", "*.skenav.io");
		csrBuilder.setOrganization("skenavtest");
		try {
			csrBuilder.sign(keyPair);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void createKeyStore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
		KeyStore ks = KeyStore.getInstance("pkcs12");
		ks.load(null,null);
		FileInputStream is = new FileInputStream("cert-chain.crt");

	}
}
