package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.Device;
import at.spengergasse.spring_thymeleaf.entities.DeviceRepository;
import at.spengergasse.spring_thymeleaf.entities.PatientRepository;
import at.spengergasse.spring_thymeleaf.entities.Reservation;
import at.spengergasse.spring_thymeleaf.entities.ReservationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final PatientRepository patientRepository;
    private final DeviceRepository deviceRepository;

    public ReservationController(ReservationRepository reservationRepository,
                                 PatientRepository patientRepository,
                                 DeviceRepository deviceRepository) {
        this.reservationRepository = reservationRepository;
        this.patientRepository = patientRepository;
        this.deviceRepository = deviceRepository;
    }

    @GetMapping("/add")
    public String addReservation(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("devices", deviceRepository.findAll());
        return "add_reservation";
    }

    @PostMapping("/add")
    public String saveReservation(@ModelAttribute("reservation") Reservation reservation, Model model) {

        if (reservation.getPatientId() == null || reservation.getDeviceId() == null) {
            model.addAttribute("error", "Please select patient and device.");
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("devices", deviceRepository.findAll());
            return "add_reservation";
        }

        reservation.setPatient(patientRepository.findById(reservation.getPatientId()).orElseThrow());
        reservation.setDevice(deviceRepository.findById(reservation.getDeviceId()).orElseThrow());

        if (reservation.getStartTime() == null || reservation.getEndTime() == null) {
            model.addAttribute("error", "Please enter start time and end time.");
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("devices", deviceRepository.findAll());
            return "add_reservation";
        }

        if (reservation.getEndTime().isBefore(reservation.getStartTime())
                || reservation.getEndTime().isEqual(reservation.getStartTime())) {
            model.addAttribute("error", "End time must be after start time.");
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("devices", deviceRepository.findAll());
            return "add_reservation";
        }

        reservationRepository.save(reservation);
        return "redirect:/reservation/list";
    }

    @GetMapping("/list")
    public String reservationList(@RequestParam(required = false) Integer deviceId, Model model) {
        model.addAttribute("devices", deviceRepository.findAll());

        if (deviceId != null) {
            Device device = deviceRepository.findById(deviceId).orElseThrow();
            model.addAttribute("selectedDevice", deviceId);
            model.addAttribute("reservations", reservationRepository.findByDevice(device));
        } else {
            model.addAttribute("selectedDevice", null);
            model.addAttribute("reservations", reservationRepository.findAll());
        }

        return "reservation_list";
    }

    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable int id) {
        reservationRepository.deleteById(id);
        return "redirect:/reservation/list";
    }
}