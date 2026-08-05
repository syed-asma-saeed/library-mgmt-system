package com.lms;

import com.lms.enums.Genre;
import com.lms.enums.MemberType;
import com.lms.exceptions.*;
import com.lms.models.Book;
import com.lms.services.LibraryService;

import java.util.List;
import java.util.Scanner;

public class App
{
    public static void main(String[] args) {

        LibraryService lms = new LibraryService();
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========");
            System.out.println("═══ BOOK MANAGEMENT ═══");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. View All Books");
            System.out.println("4. Search Books");
            System.out.println("5. View Available Books");

            System.out.println("\n═══ MEMBER MANAGEMENT ═══");
            System.out.println("6. Add Member");
            System.out.println("7. Remove Member");

            System.out.println("\n═══ LIBRARY OPERATIONS ═══");
            System.out.println("8. Borrow Book");
            System.out.println("9. Return Book");

            System.out.println("\n═══ REPORTS ═══");
            System.out.println("10. View Overdue Records");
            System.out.println("11. View Member Borrow History");

            System.out.println("0. Exit");

            System.out.print("\nEnter choice: ");

            try {

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {

                    case 1:
                        System.out.print("Title: ");
                        String title = scanner.nextLine();

                        System.out.print("Author: ");
                        String author = scanner.nextLine();

                        System.out.println("Genres:");
                        for (Genre g : Genre.values())
                            System.out.println("- " + g.name() + " (" + g.getDisplayName() + ")");

                        System.out.print("Genre: ");
                        Genre genre = Genre.valueOf(scanner.nextLine().toUpperCase());

                        System.out.print("Total number of copies: ");
                        int totalCopies = Integer.parseInt(scanner.nextLine());

                        String isbn = lms.addBook(title, author, genre, totalCopies);

                        System.out.println("Book added successfully.");
                        System.out.println("ISBN: " + isbn);
                        break;

                    case 2:
                        System.out.print("ISBN: ");
                        lms.removeBook(scanner.nextLine());
                        System.out.println("Book removed.");
                        break;

                    case 3:
                        List<Book> allBooks = lms.getAllBooks();
                        if (allBooks.isEmpty())
                            System.out.println("No books found.");
                        else
                            allBooks.forEach(System.out::println);
                        break;

                    case 4:

                        System.out.println("1. Search by Title");
                        System.out.println("2. Search by Author");
                        System.out.println("3. Search by Genre");

                        int searchChoice = Integer.parseInt(scanner.nextLine());

                        List<Book> books;

                        switch (searchChoice) {

                            case 1:
                                System.out.print("Keyword: ");
                                books = lms.searchByTitle(scanner.nextLine());
                                books.forEach(System.out::println);
                                break;

                            case 2:
                                System.out.print("Keyword: ");
                                books = lms.searchByAuthor(scanner.nextLine());
                                books.forEach(System.out::println);
                                break;

                            case 3:
                                System.out.print("Genre: ");
                                Genre g = Genre.valueOf(scanner.nextLine().toUpperCase());
                                books = lms.searchByGenre(g);
                                books.forEach(System.out::println);
                                break;

                            default:
                                System.out.println("Invalid choice.");
                        }

                        break;

                    case 5:
                        lms.getAvailableBooks().forEach(System.out::println);
                        break;

                    case 6:
                        System.out.print("Name: ");
                        String name = scanner.nextLine();

                        System.out.print("Email: ");
                        String email = scanner.nextLine();

                        System.out.println("Member Types:");
                        for (MemberType t : MemberType.values())
                            System.out.println("- " + t);

                        System.out.print("Type: ");
                        MemberType type = MemberType.valueOf(scanner.nextLine().toUpperCase());

                        String memberId = lms.addMember(name, email, type);

                        System.out.println("Member added.");
                        System.out.println("Member ID: " + memberId);
                        break;

                    case 7:
                        System.out.print("Member ID: ");
                        lms.removeMember(scanner.nextLine());
                        System.out.println("Member removed.");
                        break;

                    case 8:
                        System.out.print("Member ID: ");
                        String mId = scanner.nextLine();

                        System.out.print("ISBN: ");
                        String bookIsbn = scanner.nextLine();

                        String recordId = lms.borrowBook(mId, bookIsbn);

                        System.out.println("Book borrowed successfully.");
                        System.out.println("Record ID: " + recordId);
                        break;

                    case 9:
                        System.out.print("Record ID: ");
                        String recId = scanner.nextLine();

                        double fine = lms.returnBook(recId);

                        System.out.println("Book returned.");

                        if (fine > 0)
                            System.out.println("Overdue fine: Rs: " + fine);
                        else
                            System.out.println("No fine. Returned on time.");

                        break;

                    case 10:
                        lms.getOverdueRecords().forEach(System.out::println);
                        break;

                    case 11:
                        System.out.print("Member ID: ");
                        lms.getMemberBorrowHistory(scanner.nextLine())
                                .forEach(System.out::println);
                        break;

                    case 0:
                        System.out.println("Thank you for using the Library Management System.");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (MemberNotFoundException e) {
                System.out.println("Member Error: " + e.getMessage());

            } catch (BookNotFoundException e) {
                System.out.println("Book Error: " + e.getMessage());

            } catch (BorrowLimitExceededException e) {
                System.out.println("Borrow Limit Error: " + e.getMessage());

            } catch (BookNotAvailableException e) {
                System.out.println("Availability Error: " + e.getMessage());

            } catch (BookAlreadyReturnedException e) {
                System.out.println("Return Error: " + e.getMessage());

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input.");

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }
}
