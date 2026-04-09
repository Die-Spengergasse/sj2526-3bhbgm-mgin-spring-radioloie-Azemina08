package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.Patient;
import at.spengergasse.spring_thymeleaf.entities.PatientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patient")
public class PatientController {

    // Repository, um auf die Datenbank zuzugreifen
    private final PatientRepository patientRepository;

    // Konstruktor-Injection von Spring
    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Zeigt alle Patienten in der Liste an
    @GetMapping("/list")
    public String patients(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        return "patlist";
    }

    // Öffnet das Formular für einen neuen Patienten
    @GetMapping("/add")
    public String addPatient(Model model) {
        model.addAttribute("patient", new Patient());
        return "add_patient";
    }

    // Speichert neuen Patienten ODER aktualisiert bestehenden Patienten
    // Wenn patient.id = 0 bzw. neu -> INSERT
    // Wenn patient.id schon vorhanden -> UPDATE
    @PostMapping("/add")
    public String addPatient(@ModelAttribute("patient") Patient patient) {
        patientRepository.save(patient);
        return "redirect:/patient/list";
    }

    // Lädt einen bestehenden Patienten in das Formular zum Bearbeiten
    @GetMapping("/edit/{id}")
    public String editPatient(@PathVariable int id, Model model) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        model.addAttribute("patient", patient);
        return "add_patient";
    }

    // Löscht einen Patienten anhand seiner ID
    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable int id) {
        patientRepository.deleteById(id);
        return "redirect:/patient/list";
    }
}