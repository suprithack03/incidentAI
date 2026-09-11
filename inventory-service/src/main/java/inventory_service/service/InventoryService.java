package inventory_service.service;

import inventory_service.entity.Inventory;
import inventory_service.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory updateInventory(Long id, Inventory updatedInventory) {
        Inventory existingInventory = getInventoryById(id);

        existingInventory.setProductId(updatedInventory.getProductId());
        existingInventory.setQuantity(updatedInventory.getQuantity());
        existingInventory.setStatus(updatedInventory.getStatus());

        return inventoryRepository.save(existingInventory);
    }

    public void deleteInventory(Long id) {
        Inventory existingInventory = getInventoryById(id);
        inventoryRepository.delete(existingInventory);
    }
}