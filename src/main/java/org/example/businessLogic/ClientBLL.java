package org.example.businessLogic;

import org.example.dataAccess.ClientDAO;
import org.example.model.Client;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business Logic Layer class for managing Client operations.
 */

public class ClientBLL {
    private ClientDAO clientDAO;
    public ClientBLL() {
        this.clientDAO = new ClientDAO();
    }
    public Client findClientById(int id) {
        Client client = (Client) clientDAO.findById(id);
        if (client == null) {
            throw new NoSuchElementException("The client with id " + id + " does not exist");
        }
        return client;
    }
    public List<Client> findAllClients() {
        return clientDAO.findAll();
    }
    public void insertClient(Client client) {
        if (client.getEmail() == null || !client.getEmail().contains("@")) {
            throw new IllegalArgumentException("Adresa de email este invalida!");
        }
        clientDAO.insert(client);
    }
    public void updateClient(Client client) {
        findClientById(client.getId());
        clientDAO.update(client);
    }
    public void deleteClient(int id) {
        findClientById(id);
        clientDAO.delete(id);
    }
}
