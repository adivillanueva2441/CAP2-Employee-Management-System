let currentPage = 0;

// Checks if page has loaded
document.addEventListener('DOMContentLoaded', () => {
    loadDepartments();
});

// fetches departments from the API and renders them
async function loadDepartments(page = 0) {
    currentPage = page;
 
    const res = await fetch(`${DEPARTMENT_API}?page=${page}&size=${PAGE_SIZE}`, {
        credentials: 'include'
    });
 
    const message = sessionStorage.getItem('successMessage');
    if (message) {
        showAlert(message, 'success');
        sessionStorage.removeItem('successMessage'); // clear after showing

    }
    
    const data = await res.json();
 
    renderTable(data);
    renderPagination(data);
}

// builds the table rows from the API response
function renderTable(data){
    const tbody = document.getElementById('departmentTableBody');

    if(data.content.length === 0 ){
        tbody.innerHTML = `
        <tr>
            <td colspan = "4" class = "text-center text-muted py-5"> No departments found.
            </td>
        </tr>`;

        return;
    }

    // loop through each employee and build a table row
    tbody.innerHTML = data.content.map(department => `
        <tr>
            <td>${department.departmentName}</td>
            <td>${department.employeeCount}</td>
            <td>
                <a href="/department-form.html?id=${department.departmentId}" class="btn btn-outline-primary btn-sm me-1">
                    <i class="bi bi-pencil"></i>
                </a>
                <button class="btn btn-outline-danger btn-sm" onclick="deleteDepartment(${department.departmentId}, '${department.departmentName}')">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// builds the pagination buttons
function renderPagination(data) {
    const { number, totalPages, totalElements, size } = data;

    // show "Showing 1-10 of 100 employees"
    const start = number * size + 1;
    const end = Math.min((number + 1) * size, totalElements);
    document.getElementById('paginationInfo').textContent =
        totalElements > 0 ? `Showing ${start}–${end} of ${totalElements} employees` : '';

    const pagination = document.getElementById('pagination');

    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    let html = '';

    // previous button
    html += `
        <li class="page-item ${number === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="event.preventDefault(); loadDepartments(${number - 1})">
                <i class="bi bi-chevron-left"></i>
            </a>
        </li>`;

    // page number buttons
    for (let i = 0; i < totalPages; i++) {
        html += `
            <li class="page-item ${i === number ? 'active' : ''}">
                <a class="page-link" href="#" onclick="event.preventDefault(); loadDepartments(${i})">${i + 1}</a>
            </li>`;
    }

    // next button
    html += `
        <li class="page-item ${number === totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="event.preventDefault(); loadDepartments(${number + 1})">
                <i class="bi bi-chevron-right"></i>
            </a>
        </li>`;

    pagination.innerHTML = html;
}

// Deletes a department by id
async function deleteDepartment(departmentId) {
    if (!confirm('Are you sure you want to delete this department?')) return;

    const res = await fetch(`${DEPARTMENT_API}/${departmentId}`, {
        method: 'DELETE',
        credentials: 'include'
    });

    if (res.ok) {
        const message = await res.text();
        await loadDepartments(currentPage); // Reload current page after delete
        showAlert(message, 'success');
    } else {
        const err = await res.json();
        showAlert(err.message || 'Failed to delete department.', 'danger');
    }
}

function showAlert(message, type = 'success') {
    const box = document.getElementById('alertBox');
    box.className = `alert alert-${type} alert-dismissible fade show`;
    document.getElementById('alertMessage').textContent = message;
    box.classList.remove('d-none');
    setTimeout(() => box.classList.add('d-none'), 4000);
}
