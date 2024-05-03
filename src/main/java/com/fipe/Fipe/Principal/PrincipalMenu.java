package com.fipe.Fipe.Principal;

import com.fipe.Fipe.Model.DataDto;
import com.fipe.Fipe.Model.ModelsDto;
import com.fipe.Fipe.Model.VehicleDataDto;
import com.fipe.Fipe.Service.ConvertData;
import com.fipe.Fipe.Service.HttpIntegration;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class PrincipalMenu {
    public Scanner scanner = new Scanner(System.in);
    public HttpIntegration httpIntegration = new HttpIntegration();
    public ConvertData convertData = new ConvertData();
    public String baseUrl = "https://parallelum.com.br/fipe/api/v1";

    public void showMenu() {
        System.out.println("Bem vindo a consulta de tabela FIPE!!!");

        String firstAnswer;
        DataDto brandAnswer, modelAnswer, yearsAnswer;
        do {
            showFirstMenu();
            firstAnswer = tryConvertVehicleAnswer(scanner);
        } while (firstAnswer == null);

        var json = httpIntegration.CallApi(baseUrl + "/" + firstAnswer + "/marcas");
        var brandDtoList = convertData.getList(json, DataDto.class);

        do {
            System.out.println("Escolha um código ou um nome de marca:");
            brandDtoList.forEach(dataDto -> System.out.println("Código: " + dataDto.codigo()
                    + " Marca: " + dataDto.nome()));
            brandAnswer = tryConvertDataDtoAnswer(scanner, brandDtoList, true);
        } while (brandAnswer == null);

        json = httpIntegration.CallApi(baseUrl + "/" + firstAnswer + "/marcas/" + brandAnswer.codigo() + "/modelos");
        var searchedBrandDtoList = convertData.getData(json, ModelsDto.class);

        do {
            System.out.println("Escolha um código ou um nome do modelo:");
            searchedBrandDtoList.modelos().forEach(dataDto -> System.out.println("Código: " + dataDto.codigo()
                    + " Modelo: " + dataDto.nome()));
            modelAnswer = tryConvertDataDtoAnswer(scanner, searchedBrandDtoList.modelos(), true);
        } while (modelAnswer == null);

        json = httpIntegration.CallApi(baseUrl + "/" + firstAnswer + "/marcas/" + brandAnswer.codigo() + "/modelos/" + modelAnswer.codigo() + "/anos");
        var modelYearsList = convertData.getList(json, DataDto.class);

        do {
            System.out.println("Escolha um ano:");
            modelYearsList.forEach(dataDto -> System.out.println("Código: " + dataDto.codigo()
                    + " Ano: " + dataDto.nome()));
            yearsAnswer = tryConvertDataDtoAnswer(scanner, modelYearsList, false);
        } while (yearsAnswer == null);

        json = httpIntegration.CallApi(baseUrl + "/" + firstAnswer + "/marcas/" + brandAnswer.codigo() + "/modelos/" + modelAnswer.codigo() + "/anos/" + yearsAnswer.codigo());
        var vehicleData = convertData.getData(json, VehicleDataDto.class);
        System.out.println("DADOS DA TABELA FIPE" +
                "\nMarca: " + vehicleData.Marca() +
                "\nModelo: " + vehicleData.Modelo() +
                "\nAno do modelo: " + vehicleData.AnoModelo() +
                "\nCombustível: " + vehicleData.Combustivel() +
                "\nCódigo Fipe: " + vehicleData.CodigoFipe() +
                "\nValor: " + vehicleData.Valor()
        );

    }

    private void showFirstMenu() {
        System.out.println("Por favor, escolha uma das opções abaixo:");
        System.out.println("1 - Carros");
        System.out.println("2 - Motos");
        System.out.println("3 - Caminhões");
    }

    private @Nullable String tryConvertVehicleAnswer(Scanner scanner) {
        var answer = scanner.nextLine();
        if (answer.contains("car") || answer.equals("1")) return "carros";
        if (answer.contains("mot") || answer.equals("2")) return "motos";
        if (answer.contains("cam") || answer.equals("3")) return "caminhoes";

        System.out.println("Escolha uma opção válida por favor!");
        return null;
    }

    private @Nullable DataDto tryConvertDataDtoAnswer(Scanner scanner, List<DataDto> dataDtoList, boolean codeInt) {
        var answer = scanner.nextLine();
        int answerInt = 0;
        boolean converted = true;
        Optional<DataDto> search;
        try {
            answerInt = Integer.valueOf(answer);
        } catch (NumberFormatException e) {
            converted = false;
        }

        if (converted && codeInt) {
            int finalAnswerInt = answerInt;
            search = dataDtoList.stream()
                    .filter(b -> Integer.parseInt(b.codigo()) == finalAnswerInt)
                    .findFirst();
        } else {
            search = dataDtoList.stream()
                    .filter(b -> b.nome().toLowerCase().contains(answer.toLowerCase()))
                    .findFirst();
        }

        if (search.isPresent()) return search.get();

        System.out.println("Escolha uma opção válida por favor!");
        return null;
    }
}
